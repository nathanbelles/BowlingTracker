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
import com.warnea.bowlingtracker.models.Series;
import com.warnea.bowlingtracker.ui.dialogs.EditSeriesDialog;
import com.warnea.bowlingtracker.ui.runnables.SeriesOkRunnable;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class SeriesListAdapter extends RecyclerView.Adapter<SeriesListAdapter.MyViewHolder> {
    private List<Series> values;
    private static onClickListener onClickListener;
    private Context context;

    public SeriesListAdapter(Context context, List<Series> values) {
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
    public SeriesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_series, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        holder.dateTextView.setText(dateFormatter.format(values.get(position).getDate()));
        holder.weekTextView.setText(
                String.format("%s %d",
                        context.getString(R.string.list_adapter_series_series),
                        values.size() - position));
        holder.scratchSeriesTextView.setText(
                String.format("%s: %d",
                        context.getString(R.string.list_adapter_series_scratch_total),
                        values.get(position).getScratchTotal()));
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnCreateContextMenuListener {
        private TextView dateTextView = null;
        private TextView scratchSeriesTextView = null;
        private TextView weekTextView = null;

        public MyViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.list_series_textview_date);
            weekTextView = itemView.findViewById(R.id.list_series_textview_week);
            scratchSeriesTextView = itemView.findViewById(R.id.list_series_textview_scratch_series);
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

                            Fragment frag = manager.findFragmentByTag("fragment_edit_series");
                            if (frag != null) {
                                manager.beginTransaction().remove(frag).commit();
                            }

                            EditSeriesDialog newSeriesDialog = new EditSeriesDialog(
                                    new SeriesOkRunnable() {
                                        @Override
                                        public void run() {
                                            values.get(index).setValues(
                                                    this.getDate(), this.getHdcpPerGame());
                                            notifyItemChanged(index);
                                        }
                                    },
                                    new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    },
                                    values.get(index).getLeague(),
                                    values.get(index)
                            );
                            newSeriesDialog.show(manager, "fragment_edit_series");
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

    public void setOnItemClickListener(onClickListener onclicklistner) {
        SeriesListAdapter.onClickListener = onclicklistner;
    }

    public void removeAt(int position) {
        values.get(position).delete();
        values.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, values.size());
        notifyDataSetChanged();
    }





    public interface onClickListener {
        void onItemClick(int position, View v);
    }
}