package com.aryvart.uticianvender.provider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by android3 on 30/6/16.
 */
public class ProviderPlaceAutoComplete extends PlaceAutocompleteFragment {
    TextView tv;
    // ImageView im;
    Marker mark;
    Double lat = 0.0, longi = 0.0;
    LatLng TutorialsPoints = new LatLng(lat, longi);
    ProviderPlaceAutoComplete customPlaceAutoCompleteFragment;
    Place places;
    GeneralData gD;
    GoogleMap googleMap;
    String str_address;
    private EditText editSearch;
    private View zzaRh;
    private View zzaRi;
    private EditText zzaRj;
    @Nullable
    private LatLngBounds zzaRk;
    @Nullable
    private AutocompleteFilter zzaRl;
    @Nullable
    private PlaceSelectionListener zzaRm;

    ProgressDialog dialog;

    public ProviderPlaceAutoComplete() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View var4 = null;
        try {
            var4 = inflater.inflate(R.layout.providerplaceautocomplet, container, false);


            editSearch = (EditText) var4.findViewById(R.id.editWorkLocation);

            editSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zzzG();
                }
            });
            str_address = editSearch.getText().toString();


            editSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zzzG();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return var4;
    }


    public void onDestroyView() {
        this.zzaRh = null;
        this.zzaRi = null;
        this.editSearch = null;
        super.onDestroyView();
    }

    public void setBoundsBias(@Nullable LatLngBounds bounds) {
        this.zzaRk = bounds;
    }

    public void setFilter(@Nullable AutocompleteFilter filter) {
        this.zzaRl = filter;
    }

    public void setText(CharSequence text) {
        this.editSearch.setText(text);
        //this.zzzF();
    }

    public void setHint(CharSequence hint) {
        this.editSearch.setHint(hint);
        this.zzaRh.setContentDescription(hint);
    }

    public void setOnPlaceSelectedListener(PlaceSelectionListener listener) {
        this.zzaRm = listener;


    }

    private void zzzF() {
        boolean var1 = !this.editSearch.getText().toString().isEmpty();
        //this.zzaRi.setVisibility(var1?0:8);
    }

    private void zzzG() {
        try {
            int var1 = -1;

            try {
                Intent var2 = (new PlaceAutocomplete.IntentBuilder(2)).setBoundsBias(this.zzaRk).setFilter(this.zzaRl).zzfd(this.editSearch.getText().toString()).zzkU(1).build(this.getActivity());

                this.startActivityForResult(var2, 1);
            } catch (GooglePlayServicesRepairableException var3) {
                var1 = var3.getConnectionStatusCode();
                Log.e("Places", "Could not open autocomplete activity", var3);
            } catch (GooglePlayServicesNotAvailableException var4) {
                var1 = var4.errorCode;
                Log.e("Places", "Could not open autocomplete activity", var4);
            }

            if (var1 != -1) {
                GoogleApiAvailability var5 = GoogleApiAvailability.getInstance();
                var5.showErrorDialogFragment(this.getActivity(), var1, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 1) {

                Log.e("PL", "i'm in onActivityResult 1 : " + resultCode);

                if (resultCode == -1) {

                    Log.e("PL", "i'm in onActivityResult 2 : " + resultCode);

                    Place var4 = PlaceAutocomplete.getPlace(this.getActivity(), data);
                    if (mark != null) {
                        mark.remove();
                    }


                    gD = new GeneralData();




                    //Get the Address from the lat and lang

                    String result = "";
                    try {
                        Geocoder geocoder;
                        List<Address> addressList = null;
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        addressList = geocoder.getFromLocation(Double.parseDouble(String.valueOf(var4.getLatLng().latitude)), Double.parseDouble(String.valueOf(var4.getLatLng().longitude)), 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                sb.append(address.getAddressLine(i)).append("\n");
                            }
                            sb.append(address.getLocality()).append("\n");
                            sb.append(address.getPostalCode()).append("\n");
                            sb.append(address.getCountryName());
                            result = sb.toString();
                            Log.e("AddrVal", result);
                            editSearch.setText(result);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (this.zzaRm != null) {
                        this.zzaRm.onPlaceSelected(var4);
                        Log.e("PL", "" + var4);
                    }

                    Log.i("GGG", "CustomPlaceAutoCompleteFragment : " + var4.getAddress().toString());


                    gD.pro_searchAddress = var4.getAddress().toString();
                    gD.pro_searchLatitutde = String.valueOf(var4.getLatLng().latitude);
                    gD.pro_searchLongitude = String.valueOf(var4.getLatLng().longitude);


                    //gD.strAddress= var4.getAddress().toString();

                    this.setText(var4.getName().toString());
                } else if (resultCode == 2) {
                    Status var5 = PlaceAutocomplete.getStatus(this.getActivity(), data);
                    if (this.zzaRm != null) {
                        this.zzaRm.onError(var5);
                        Log.e("place---->", "" + var5);
                    }
                }
            }

            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void drawMarker(LatLng point, final String str_address, final String strProviderId, final String strResp) {
        try {
            // Creating an instance of MarkerOptions
            final MarkerOptions markerOptions = new MarkerOptions();
            gD.googleMapGeneral.setInfoWindowAdapter(new MyInfoWindowAdapter());
            // Setting latitude and longitude for the marker
            markerOptions.position(point).snippet(str_address).title(strProviderId).icon(BitmapDescriptorFactory.fromResource(R.drawable.igreen));
            // Adding marker on the Google Map

            googleMap.addMarker(markerOptions);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.user_current_loc_addr_alert, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.text_address));
            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}