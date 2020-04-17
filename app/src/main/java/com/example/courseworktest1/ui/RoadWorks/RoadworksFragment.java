package com.example.courseworktest1.ui.RoadWorks;
// This is the import list for the roadworks fragment
// Joshua Cowan S1823790
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.courseworktest1.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RoadworksFragment extends Fragment { //call must extend the fragment command as it splits this java class into a fragment

    private ArrayList<String> roadArrayList = new ArrayList<String>();

    private ListView roadListView;
    private ArrayAdapter<String> roadAdaptor;

    //This method is for when the fragment is selected
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.example.courseworktest1.ui.RoadWorks.RoadworksViewModel roadworksViewModel = ViewModelProviders.of(this).get(RoadworksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_roadworks, container, false);

        //creates a new asynchronous search method
        new AsyncSearch().execute();

        //creates a list and edit view
        roadListView = (ListView) root.findViewById(R.id.roadView);
        EditText roadFilter = (EditText) root.findViewById(R.id.Current_editText);

        //creates a filter so the data can listen to the commands
        roadFilter.addTextChangedListener(new TextWatcher() {
            @Override
            //what occurs before the text is changed, empty due to this being handled later on
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // this is for allowing the fragment to display the information from the dataset while being *HORIZONTAL*
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String str = s.toString();
                    if (!str.equals("")) {
                        (RoadworksFragment.this).roadAdaptor.getFilter().filter(str);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return root;
    }
    //starts a new asynchronous search method
    private class AsyncSearch extends AsyncTask<Integer, Integer, ArrayList<String>>{

        //creates an arraylist for background processes
        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            String RoadworksURL = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
            try {//this whole try catch is for parsing the data from the rss feed to the app
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(RoadworksURL);
                document.getDocumentElement().normalize();
                //new nodelist for creating a section in the apps screen for a specific piece of data parsed from the rss feed
                NodeList NL = document.getElementsByTagName("item");
                //ensures data can be shown on the screen if the feed is empty
                if (NL.getLength() >=1) {

                    //counter for data in the rss feed
                    for (int counter = 0; counter < NL.getLength(); counter++) {
                        Node currentNode = NL.item(counter);
                        if (currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode != null) {

                            Element CurrentElement = (Element) currentNode;
                            //takes the title and description from the feed and converts them to string
                            String title, description;

                            //the next 2 if statements are for if parts of the feed are empty, this is to ensure no errors occur while parsing
                            if (CurrentElement.getElementsByTagName("title").item(0) != null) {
                                title = CurrentElement.getElementsByTagName("title").item(0).getTextContent();
                            } else {
                                title = "title not available";
                            }
                            if (CurrentElement.getElementsByTagName("description").item(0) != null) {
                                description = CurrentElement.getElementsByTagName("description").item(0).getTextContent();
                            } else {
                                description = "description not available";
                            }
                            String incidentInformation = "\n" + title + "\n\n" + description + "\n";

                            //adds the next piece of parsed data into the app
                            roadArrayList.add(incidentInformation);

                        } else {//allows for feed to be empty and not cause an error
                            roadArrayList.add("No incidents available to display");
                        }
                    }
                }else {
                    roadArrayList.add("No incidents available to display");
                }
                return roadArrayList;

                //this catch gives the developer an easier job of finding the error when the app is running
            } catch(Exception currentException) {
                System.out.println("error1R " + currentException.getMessage());
            }
            return null;
        }

        //pulls the parsed data from the feed into the app
        protected void onPostExecute(ArrayList<String> roadData) {
            try {
                roadAdaptor = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        roadData

                );
                roadListView.setAdapter(roadAdaptor);
            } catch (Exception currentException) {
                System.out.println("error2R " + currentException.getMessage());
            }
        }

        //ensures that the adaptor isn't empty and populates the listview with the adaptor data
        protected void onPreExecute() {
           try{
               roadAdaptor = (ArrayAdapter<String>) roadListView.getAdapter();
           } catch (Exception Exception) {
               System.out.println("error3R " + Exception.getMessage());

           }
        }
    }
}
