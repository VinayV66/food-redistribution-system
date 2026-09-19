package com.foodrescue.service;

import com.foodrescue.dto.request.CreateDonationRequest;
import com.foodrescue.dto.response.DonationResponse;
import com.foodrescue.dto.response.PagedResponse;
import com.foodrescue.enums.DonationStatus;
import com.foodrescue.enums.FoodCategory;
import org.springframework.data.domain.Pageable;

public interface FoodDonationService {
    DonationResponse createDonation(CreateDonationRequest request, Long donorId);
    DonationResponse getDonationById(Long id);
    PagedResponse<DonationResponse> getAllDonations(DonationStatus status, FoodCategory category,
                                                    Boolean vegetarian, Double minQty, Double maxQty,
                                                    Pageable pageable);
    PagedResponse<DonationResponse> getMyDonations(Long donorId, DonationStatus status, Pageable pageable);
    DonationResponse updateDonation(Long id, CreateDonationRequest request, Long donorId);
    void cancelDonation(Long id, Long donorId);
}
