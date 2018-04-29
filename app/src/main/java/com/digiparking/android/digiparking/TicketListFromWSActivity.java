package com.digiparking.android.digiparking;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.digiparking.android.digiparking.modele.Ticket;
import com.digiparking.android.digiparking.service.WebService;
import com.digiparking.android.digiparking.util.TicketListAdapter;

import java.util.ArrayList;

public class TicketListFromWSActivity extends AppCompatActivity {

    private static int GET_DETAIL_REQUEST = 1;
    ProgressDialog dialog;
    ArrayList<Ticket> ticketsList;
    ListView mListView;
    TicketListAdapter ticketListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list_from_ws);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ticket_ws);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.ticket_ws_listView);
        dialog = new ProgressDialog(this);
        AsyncWebService aws = new AsyncWebService();
        aws.execute();
    }

    private class AsyncWebService extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ticketsList = new WebService().getTickets();
            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting tickes ...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Todo : owner textview
            //TextView owner = (TextView) findViewById(R.id.ticket_detail_owner);
            ticketListAdapter = new TicketListAdapter(getApplicationContext(), ticketsList);
            mListView.setAdapter(ticketListAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intentToDetail = new Intent(getApplicationContext(), TicketDetailActivity.class);
                    Ticket theClickedTicket = (Ticket) mListView.getItemAtPosition(position);
                    intentToDetail.putExtra("theClickedTicketHistoric", theClickedTicket);
                    intentToDetail.putExtra("activityFrom", "ADMIN_CHECK");
                    startActivityForResult(intentToDetail, GET_DETAIL_REQUEST);
                }
            });
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Recherche par immatriculation ou date");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ticketListAdapter.search(newText);

                return false;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_DETAIL_REQUEST && requestCode==RESULT_OK){
            Log.i("back to", "ticket list ws");
        }

    }
}
