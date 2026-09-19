package com.foodrescue.service.impl;

import com.foodrescue.entity.FoodDonation;
import com.foodrescue.entity.NgoProfile;
import com.foodrescue.repository.NgoProfileRepository;
import com.foodrescue.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Matching algorithm that ranks NGOs for a food donation.
 *
 * Scoring criteria (modular — easy to extend):
 *  1. Distance (Haversine formula) — primary sort factor
 *  2. Only approved NGOs are eligible
 *  3. NGOs with sufficient capacity are preferred
 *
 * The Haversine formula calculates the great-circle distance
 * between two GPS coordinates on Earth.
 *
 * Future improvements:
 *  - Add ML-based scoring
 *  - Consider NGO food category preferences
 *  - Track NGO response rate
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    private final NgoProfileRepository ngoProfileRepository;

    /** Earth's radius in kilometers. */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /** Maximum search radius in kilometers. */
    private static final double MAX_RADIUS_KM = 50.0;

    @Override
    public List<NgoProfile> findSuitableNgos(FoodDonation donation, int maxResults) {
        // Only consider approved NGOs
        List<NgoProfile> approvedNgos = ngoProfileRepository.findByApprovedTrue();

        if (approvedNgos.isEmpty()) {
            log.warn("No approved NGOs found for matching.");
            return List.of();
        }

        // Check if donation has GPS coordinates for distance-based matching
        boolean hasDonationCoords = donation.getLatitude() != null && donation.getLongitude() != null;

        return approvedNgos.stream()
                // Filter 1: Must have capacity (if capacity is set)
                .filter(ngo -> ngo.getDailyCapacity() == null ||
                               ngo.getDailyCapacity() >= donation.getQuantity())
                // Filter 2: Only NGOs within range (if coordinates available)
                .filter(ngo -> {
                    if (!hasDonationCoords || ngo.getLatitude() == null || ngo.getLongitude() == null) {
                        return true; // No GPS data: include all
                    }
                    double dist = calculateDistance(
                            donation.getLatitude(), donation.getLongitude(),
                            ngo.getLatitude(), ngo.getLongitude());
                    return dist <= MAX_RADIUS_KM;
                })
                // Sort by distance (closest first)
                .sorted(Comparator.comparingDouble(ngo -> {
                    if (!hasDonationCoords || ngo.getLatitude() == null || ngo.getLongitude() == null) {
                        return Double.MAX_VALUE; // No GPS: put at end
                    }
                    return calculateDistance(
                            donation.getLatitude(), donation.getLongitude(),
                            ngo.getLatitude(), ngo.getLongitude());
                }))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    /**
     * Haversine formula: calculates the straight-line distance
     * between two GPS points on Earth's surface.
     *
     * @param lat1 latitude of point 1 (degrees)
     * @param lon1 longitude of point 1 (degrees)
     * @param lat2 latitude of point 2 (degrees)
     * @param lon2 longitude of point 2 (degrees)
     * @return distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(radLat1) * Math.cos(radLat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
