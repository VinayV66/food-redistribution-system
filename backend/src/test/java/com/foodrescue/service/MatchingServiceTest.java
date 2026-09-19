package com.foodrescue.service;

import com.foodrescue.service.impl.MatchingServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Haversine distance formula in MatchingServiceImpl.
 */
class MatchingServiceTest {

    @Test
    void testHaversineDistanceSamePoint() {
        // Distance from a point to itself should be 0
        double distance = MatchingServiceImpl.calculateDistance(12.9716, 77.5946, 12.9716, 77.5946);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void testHaversineDistanceBangaloreToMumbai() {
        // Bangalore (12.9716, 77.5946) to Mumbai (19.0760, 72.8777)
        // Approximate distance: ~845 km
        double distance = MatchingServiceImpl.calculateDistance(12.9716, 77.5946, 19.0760, 72.8777);
        assertTrue(distance > 800 && distance < 900,
                "Distance should be approximately 845 km, got: " + distance);
    }

    @Test
    void testHaversineDistanceNearbyPoints() {
        // Two points about 5 km apart in Bangalore
        double distance = MatchingServiceImpl.calculateDistance(12.9716, 77.5946, 12.9200, 77.6100);
        assertTrue(distance > 4 && distance < 8,
                "Nearby points should be 4-8 km apart, got: " + distance);
    }
}
