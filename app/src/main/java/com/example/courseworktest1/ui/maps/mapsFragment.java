package com.example.courseworktest1.ui.maps;
//imports for the maps fragment
// Joshua Cowan S1823790
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.courseworktest1.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//public class to start maps fragment, extends to the function fragment as it is it's own section in the navbar
public class mapsFragment extends Fragment {

    //declares main variables for the fragment here
    private MapView MW;
    private GoogleMap googleMap;

    //arraylist announced here
    ArrayList<String> mapAL = new ArrayList<String>();
    ArrayAdapter mapAdaptor;

    //similar to every other onCreateView method, functions occur when the app is started
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        MW = (MapView) root.findViewById(R.id.mapOverview);
        MW.onCreate(savedInstanceState);
        MW.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        //asynchronously opens and operates the map
        MW.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap GM) {
                googleMap = GM;

                googleMap.setMyLocationEnabled(true);
                new mapsFragment.AsyncTask().execute();
            }
        });
        return root;
    }

    //the next 4 override methods are just in case a fatal error occurs with the device
    @Override
    public void onResume() {
        super.onResume();
        MW.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MW.onPause();
    }

    @Override
    public void onDestroy() {
        MW.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        MW.onLowMemory();
        super.onLowMemory();
    }



    //regular async method for conducting functions in the background
    private class AsyncTask extends android.os.AsyncTask<Integer, Integer, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            String urlSource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
            try {
                DocumentBuilderFactory factory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder b = factory.newDocumentBuilder();
                Document document = b.parse(urlSource);

                document.getDocumentElement().normalize();

                NodeList NL = document.getElementsByTagName("item");

                for (int counter = 0; counter < NL.getLength(); counter++) {
                    Node node = NL.item(counter);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;

                        String description, title, RSSGeo;

                        title = "Road Incident";

                        if (eElement.getElementsByTagName("description").item(0) != null) {
                            description = eElement.getElementsByTagName("description").item(0).getTextContent();
                        } else {
                            description = "No Description";
                        }

                        if (eElement.getElementsByTagName("georss:point").item(0) != null) {
                            RSSGeo = eElement.getElementsByTagName("georss:point").item(0).getTextContent();
                        } else {
                            RSSGeo = "No Description";
                        }


                        mapAL.add(title + "/!!/" + description + "/!!/" + RSSGeo);
                    }
                }

                return mapAL;
            } catch (Exception Exception) {
                System.out.println(Exception.getMessage());
            }

            return null;

        }

        //operates the map and displays the markers based on the rss feed data
        protected void onPostExecute(ArrayList<String> result) {



                for (String strTemp : result) {

                    System.out.println(result);
                    String StringSplit = strTemp.toString();

                    String[] arrOfStr = StringSplit.split("/!!/");

                    String StringSplit2 = arrOfStr[2].toString();
                    String[] latLong2 = StringSplit2.split(" ");

                    String latLong3 = latLong2[1].replace(",", "");

                    System.out.println(arrOfStr[0]);
                    System.out.println(arrOfStr[1]);

                    LatLng LatLng = new LatLng(Double.parseDouble(latLong2[0]), Double.parseDouble(latLong3));
                    googleMap.addMarker(new MarkerOptions().position(LatLng).title(arrOfStr[0]).snippet(arrOfStr[1]));


                }

            }
        }
    }


