/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androiddj;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class DeviceListFragment extends ListFragment implements PeerListListener {

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private boolean visible = false ;
    private WifiP2pDevice device;
    private String hostType="";
    private String host_pwd=null;
    public String client_pwd="";
    int request_code = 1;
    private String e_tag = "tag";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);

        mContentView.findViewById(R.id.start_party).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hostType.equals("Client"))
                {
                    if (host_pwd.equals("open"))
                    {
                        // mContentView.findViewById(R.id.start_party).setEnabled(false);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Enter Password");

                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        input.setText("open");

                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            int set = 0;
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                client_pwd = input.getText().toString();

                                Runnable pwdcheck = new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            String s = DeviceDetailFragment.info.groupOwnerAddress.getHostAddress();

                                            Socket socket = new Socket(s, 6666); // Create and connect the socket
                                            OutputStream dout = socket.getOutputStream();
                                            InputStream ir = socket.getInputStream();
                                            BufferedReader br = new BufferedReader(new InputStreamReader(ir));

                                            PrintWriter pw = new PrintWriter(dout);
                                            pw.println(client_pwd);
                                            pw.flush();
                                            Log.d("HOST", "1");
                                            String passed = br.readLine();
                                            Log.d("HOST", "2" + passed);
                                            if (passed.equals("TRUE")) {
                                                Intent intent = new Intent(getActivity(), ClientView.class);
                                                startActivityForResult(intent, request_code);

                                            } else {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getActivity(),"Password Incorrect.",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            socket.close();

                                        } catch (SocketException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                };

                                Thread cpwd = new Thread(pwdcheck);
                                cpwd.start();
                                try {
                                    cpwd.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                    else
                    {
                        Intent intent = new Intent(getActivity(), ClientView.class);
                        startActivityForResult(intent, request_code);
                    }
                }
                else if(hostType.equals("Host"))
                {
                    if (true) {
                        mContentView.findViewById(R.id.start_party).setEnabled(false);

                        Runnable recepwd = new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    ServerSocket ss = new ServerSocket(6666);

                                    while (true) {
                                        try {
                                            final Socket s = ss.accept();//establishes connection

                                            Runnable clientcheck = new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        pwdcheck(s);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            };

                                            Thread clienthread = new Thread(clientcheck);
                                            clienthread.start();
                                        } catch (IOException io) {
//                                        ss.close();
                                            io.printStackTrace();
                                        }
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        Thread recepwdth = new Thread(recepwd);
                        recepwdth.start();
                    }

                    Intent intent = new Intent(getActivity(),HostView.class);
                    startActivity(intent);
                }
            }
        });
        return mContentView;
    }

    public synchronized void pwdcheck (Socket s) throws IOException {
        InputStream ir = s.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(ir));
        OutputStream dout = s.getOutputStream();
        PrintWriter pw = new PrintWriter(dout);
        String str = br.readLine();
        Log.d("HOST", "Receving Pwd to host " + str + " hostpwd: " + host_pwd);

        if (str.equals(host_pwd)) {
            pw.println("TRUE");
            pw.flush();
        } else {
            pw.println("FALSE");
            pw.flush();
        }

        s.close();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(resultCode == 1 ) {     //  CODE TO DELETE ITEM
            super.onActivityResult(requestCode, resultCode, intent);

            ((WiFiDirectActivity)getActivity()).disconnect();
            Toast.makeText(getActivity(), "Disconnected from party", Toast.LENGTH_SHORT).show();
            mContentView.findViewById(R.id.start_party).setVisibility(View.GONE);
            Log.d("leave party","Leave party");
        }
        else
        {
            Toast.makeText(getActivity(), "Some error has occurred while leaving party.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * @return this device
     */
    public WifiP2pDevice getDevice() {
        return device;
    }

    private static String getDeviceStatus(int deviceStatus) {
        Log.d(WiFiDirectActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    public void setHostType(String type, String password)
    {
        hostType = type;
        host_pwd = password;
    }

    /**
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(hostType.equals("Client"))
        {
            WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
            final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_detail);
            if(visible) {
                Log.i(e_tag,"visible") ;
                fragment.getView().setVisibility(View.INVISIBLE);
                visible=false;
            }
            else {
                Log.i(e_tag,"not visible") ;
                fragment.getView().setVisibility(View.VISIBLE);
                ((DeviceActionListener) getActivity()).showDetails(device);
                visible=true;
            }
        }

    }

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

        private List<WifiP2pDevice> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
                if (top != null) {

                    top.setText(device.deviceName);
                }
                if (bottom != null) {
                    bottom.setText(getDeviceStatus(device.status));
                }
            }

            return v;

        }
    }

    /**
     * Update UI for this device.
     * 
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(WiFiDirectActivity.TAG, "No devices found");
            return;
        }
        else if(getDevice()!=null && device.status == WifiP2pDevice.CONNECTED)
        {
            mContentView.findViewById(R.id.start_party).setVisibility(View.VISIBLE);
        }
    }

    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    /**
     * 
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {

        void showDetails(WifiP2pDevice device);

        void cancelDisconnect();

        void connect(WifiP2pConfig config);

        void disconnect();
    }

}
