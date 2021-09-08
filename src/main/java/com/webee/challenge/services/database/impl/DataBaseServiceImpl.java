package com.webee.challenge.services.database.impl;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DatabaseException;
import com.webee.challenge.constants.Constants;
import com.webee.challenge.controllers.DeviceMonitoringController;
import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;
import com.webee.challenge.services.database.DataBaseService;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataBaseServiceImpl implements DataBaseService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceMonitoringController.class);

    @Value("${jsonPath}")
    private String jsonPath;

    private Firestore firestoreDB;

    @Autowired
    public void setFirestoreDB() {
        try {
            FileInputStream serviceAccount = new FileInputStream("C:\\Users\\Usuario\\Desktop\\ChallengeWebee\\src\\main\\resources\\authenticationJson\\challengewebee-firebase.json");
            //FileInputStream serviceAccount = new FileInputStream(jsonPath);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            firestoreDB = FirestoreClient.getFirestore();
        } catch (Exception e) {
            LOG.error("Problem while setting the connection: " + e.getMessage());
        }
    }

    @Override
    public void saveDevice(DeviceRequest deviceRequest) {
        DocumentReference docRef = firestoreDB.collection(Constants.COLLECTION).document();
        Map<String, Object> data = new HashMap<>();

        data.put("macAddress", deviceRequest.getMacAddress());
        data.put("date", deviceRequest.getDate());
        data.put("id", generateRandomId(10));

        ApiFuture<WriteResult> result = docRef.set(data);
        try {
            LOG.info("Update time {}", result.get().getUpdateTime());
        } catch (Exception exc) {
            LOG.error("Something went wrong while saving the device in the database");
            throw new DatabaseException("Something went wrong while saving the device in the database", exc);
        }
    }

    @Override
    public void deleteDevice(Integer id) {

    }

    @Override
    public Device searchDeviceById(Integer id) {
        Device device = Device.builder()
                .date("08-09-2021")
                .macAddress("MAC Address")
                .ID(10)
                .build();
        return device;
    }

    @Override
    public Device searchDeviceByMac(String macAddress) {
        Device device = Device.builder()
                .date("08-09-2021")
                .macAddress("MAC Address")
                .ID(10)
                .build();
        return device;
    }

    @Override
    public List<Device> searchAllDevices() {
        Device device1 = Device.builder()
                .date("08-09-2021")
                .macAddress("MAC Address")
                .ID(10)
                .build();
        Device device2 = Device.builder()
                .date("08-09-2021")
                .macAddress("MAC Address")
                .ID(10)
                .build();
        List<Device> deviceList = new ArrayList<>();
        deviceList.add(device1);
        deviceList.add(device2);
        return deviceList;
    }

    private String generateRandomId(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z').build();
        return generator.generate(length);
    }
}



