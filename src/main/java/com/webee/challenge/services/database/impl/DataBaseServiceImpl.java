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
    public void deleteDevice(String id) {

    }


    @Override
    public Device searchDeviceById(String id) {
        CollectionReference resultados = firestoreDB.collection(Constants.COLLECTION);
        Query query = resultados.whereEqualTo("id", id);
        ApiFuture<QuerySnapshot> futureQuerySnapshot = query.get();
        QuerySnapshot querySnapshot = null;
        List<QueryDocumentSnapshot> documentList = null;

        try {
            LOG.info("Searching for device with {} ID", id);
            querySnapshot = futureQuerySnapshot.get();
            documentList = querySnapshot.getDocuments();

        } catch (Exception exc) {
            String errorMessage = "There was a problem while searching for " + id + " ID";
            LOG.error(errorMessage);
            throw new DatabaseException(errorMessage, exc);
        }

        if (documentList.size() != 1) {
            throw new DatabaseException("Found " + documentList.size() + " devices with that ID");
        }

        DocumentSnapshot document2 = documentList.get(0);
        return document2.toObject(Device.class);
    }

    @Override
    public Device searchDeviceByMac(String macAddress) {
        CollectionReference resultados = firestoreDB.collection(Constants.COLLECTION);
        Query query = resultados.whereEqualTo("macAddress", macAddress);
        ApiFuture<QuerySnapshot> futureQuerySnapshot = query.get();
        QuerySnapshot querySnapshot = null;
        List<QueryDocumentSnapshot> documentList = null;

        try {
            LOG.info("Searching for device with {} MAC Address", macAddress);
            querySnapshot = futureQuerySnapshot.get();
            documentList = querySnapshot.getDocuments();
        } catch (Exception exc) {
            String errorMessage = "There was a problem while searching for " + macAddress + "MAC address";
            LOG.error(errorMessage);
            throw new DatabaseException(errorMessage, exc);
        }

        if (documentList.size() != 1) {
            throw new DatabaseException("Found " + documentList.size() + " devices with that MAC Address");
        }

        DocumentSnapshot document2 = documentList.get(0);
        return document2.toObject(Device.class);
    }

    @Override
    public List<Device> searchAllDevices() {
        ApiFuture<QuerySnapshot> query = firestoreDB.collection(Constants.COLLECTION).get();
        QuerySnapshot querySnapshot = null;

        try {
            querySnapshot = query.get();

        } catch (Exception exc) {
            LOG.error("Something went wrong while reading the database");
            throw new DatabaseException("Something went wrong while reading the database", exc);
        }

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        return generarListaResult(documents);
    }

    private List<Device> generarListaResult(List<QueryDocumentSnapshot> documents) {
        List<Device> lista = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            lista.add(document.toObject(Device.class));
        }
        return lista;
    }

    private String generateRandomId(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z').build();
        return generator.generate(length);
    }
}



