/*
 * Bowling Tracker Android app
 * Copyright the Bowling Tracker Contributors.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.warnea.bowlingtracker.ui.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.ui.dialogs.EditLeagueDialog;
import com.warnea.bowlingtracker.ui.runnables.LeagueOkRunnable;

import java.util.List;

public class LeagueListAdapter extends RecyclerView.Adapter<LeagueListAdapter.MyViewHolder> {
    private List<League> values;
    private static onClickListener onClickListener;
    private Context context;

    public LeagueListAdapter(Context context, List<League> values) {
        super();
        this.values = values;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @NonNull
    @Override
    public LeagueListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_league, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.leagueNameTextView.setText(values.get(position).getName());
        String playerName = values.get(position).getPlayerName();

        //Display the player name text if it exists, otherwise don't
        if(!playerName.equals("")) {
            holder.playerNameTextView.setVisibility(View.VISIBLE);
            holder.playerNameTextView.setText(playerName);
        } else {
            holder.playerNameTextView.setVisibility(View.GONE);
        }

        holder.averageTextView.setText(
                String.format("%s: %d",
                        context.getString(R.string.list_adapter_league_average),
                        values.get(position).getAverage()));
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnCreateContextMenuListener {
        private TextView leagueNameTextView = null;
        private TextView playerNameTextView = null;
        private TextView averageTextView = null;

        public MyViewHolder(View itemView) {
            super(itemView);
            leagueNameTextView = itemView.findViewById(R.id.list_league_textview_league_name);
            playerNameTextView = itemView.findViewById(R.id.list_league_textview_player_name);
            averageTextView = itemView.findViewById(R.id.list_league_textview_average);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onItemClick(getAdapterPosition(), v);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu,
                                        final View view,
                                        ContextMenu.ContextMenuInfo menuInfo) {

            //Edit option on menu
            menu
                    .add(0,
                            view.getId(),
                            0,
                            context.getString(R.string.context_menu_edit))
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final int index = getAdapterPosition();
                            final FragmentActivity activity = (FragmentActivity) context;
                            FragmentManager manager = activity.getSupportFragmentManager();

                            Fragment frag = manager.findFragmentByTag("fragment_edit_league");
                            if (frag != null) {
                                manager.beginTransaction().remove(frag).commit();
                            }

                            EditLeagueDialog newLeagueDialog = new EditLeagueDialog(
                                    values.get(index),
                                    new LeagueOkRunnable() {
                                        @Override
                                        public void run() {
                                            values.get(index).setValues(
                                                    this.getName(), this.getPlayerName(),
                                                    this.getTeamName(), this.getTeamNumber());
                                            notifyItemChanged(index);
                                        }
                                    },
                                    new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    }
                            );
                            newLeagueDialog.show(manager, "fragment_edit_league");
                            return true;
                        }
                    });

            //Delete option on menu
            menu
                    .add(0,
                            view.getId(),
                            0,
                            context.getString(R.string.context_menu_delete))
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // can do something with item at position given below,
                            // viewHolder is final
                            removeAt(getAdapterPosition());
                            return true;
                        }
                    });

        }



    }

    public void setOnItemClickListener(onClickListener onClickListener) {
        LeagueListAdapter.onClickListener = onClickListener;
    }

    public void removeAt(int position) {
        values.get(position).delete();
        values.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, values.size());
    }





    public interface onClickListener {
        void onItemClick(int position, View v);
    }
}