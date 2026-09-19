package com.foodrescue.service;

import com.foodrescue.entity.FoodDonation;
import com.foodrescue.entity.NgoProfile;

import java.util.List;

/**
 * Service interface for the NGO-donation matching algorithm.
 * Kept as an interface so the algorithm can be improved or swapped later.
 */
public interface MatchingService {
    /**
     * Find the best-matching NGOs for a given food donation.
     *
     * @param donation the donation to match
     * @param maxResults maximum number of NGOs to return
     * @return ranked list of suitable NGOs (closest first)
     */
    List<NgoProfile> findSuitableNgos(FoodDonation donation, int maxResults);
}
