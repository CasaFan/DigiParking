package com.digiparking.android.digiparking.util;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.digiparking.android.digiparking.modele.Ticket;
import com.digiparking.android.digiparking.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by milk1 on 4/21/2017.
 */

// TODO : OnclickedListener
public class TicketListAdapter extends ArrayAdapter<Ticket> {

    private ArrayList<Ticket> ticketsListCopy;
    private List<Ticket> ticketListRef;
    private Context mContext;

    public TicketListAdapter(Context context, ArrayList<Ticket> ticketsList){
        super(context, 0, ticketsList);
        this.mContext = context;
        this.ticketListRef = ticketsList;
        this.ticketsListCopy = new ArrayList<>();
        this.ticketsListCopy.addAll(ticketListRef);
        Log.i("Adapt-ticketList size :", String.valueOf(this.ticketsListCopy.size()));

        //Log.i("Adapt t1"+" id :", String.valueOf(ticketsList.get(1).get_id()));
    }

    public void search(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        ticketListRef.clear();
        if (charText.length() == 0) {
            ticketListRef.addAll(ticketsListCopy);
        } else {
            for (Ticket ticket : ticketsListCopy) {
                if (ticket.getVoiture().getImmatriculation().toLowerCase(Locale.getDefault()).contains(charText)) {
                    ticketListRef.add(ticket);
                }
                if (ticket.getDate().toLowerCase(Locale.getDefault()).contains(charText)){
                    ticketListRef.add(ticket);
                }
            }
        }

        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView ticketName;
        TextView ticketDate;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Ticket ticketToDisplay = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_ticket_list, parent, false);


            //reslt = convertView;
            convertView.setTag(viewHolder);
        }else {

            viewHolder = (ViewHolder) convertView.getTag();
            //reslt = convertView;
        }
        viewHolder.ticketName = (TextView) convertView.findViewById(R.id.ticket_list_item_name);
        viewHolder.ticketDate = (TextView) convertView.findViewById(R.id.ticket_list_item_date);

        viewHolder.ticketName.setText(mContext.getString(R.string.ticketName, ticketToDisplay.get_id()));
        viewHolder.ticketDate.setText(ticketToDisplay.getDate());

        //v.setTag(ticketToDisplay);
        return convertView;
    }

    @Nullable
    @Override
    public Ticket getItem(int position) {
        return this.ticketListRef.get(position);
    }
}
