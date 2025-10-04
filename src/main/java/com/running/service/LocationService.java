package com.running.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.running.entity.Location;
import com.running.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    @Value("${google.maps.api-key}")
    private String apiKey;

    @Autowired
    private LocationRepository locationRepository;

    public String getLocationCodeFromCoordinates(double lat, double lng) {
        return "11";
        /* try {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();

            LatLng location = new LatLng(lat, lng);
            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, location)
                    .language("ko")
                    .await();

            if (results.length > 0) {
                String administrativeArea = extractAdministrativeArea(results[0]);

                // DB에서 해당 행정구역의 location_code 조회
                Location locationEntity = locationRepository.findByName(administrativeArea)
                        .orElse(null);

                if (locationEntity != null) {
                    return locationEntity.getLocationCode();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; */
    }

    private String extractAdministrativeArea(GeocodingResult result) {
        // 행정구역 추출 로직
        for (var component : result.addressComponents) {
            for (var type : component.types) {
                if (type.toString().equals("ADMINISTRATIVE_AREA_LEVEL_1")) {
                    return component.longName;
                }
            }
        }
        return null;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine 공식을 이용한 거리 계산 (km)
        final int R = 6371; // 지구 반지름 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}