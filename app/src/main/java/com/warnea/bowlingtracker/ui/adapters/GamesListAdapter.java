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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.Game;
import com.warnea.bowlingtracker.models.Series;
import com.warnea.bowlingtracker.ui.fragments.GamesFragment;

import java.util.List;

public class GamesListAdapter extends RecyclerView.Adapter<GamesListAdapter.MyViewHolder> {
    
    private List<Game> games;
    private static onClickListener onClickListener;
    private View rootView;

    public GamesListAdapter(List<Game> games) {
        super();
        this.games = games;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        rootView = recyclerView.getRootView();
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    @NonNull
    @Override
    public GamesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                            int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_game, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemView.setTag(position);

        Game game = games.get(position);
        holder.scratchTotalEditText.setText(Short.toString(game.getScratchTotal()));
        setHdcpText(games.get(position), holder.hdcpTotalTextView);
        holder.labelTextView.setText(
                String.format("%s %d: ",
                    rootView.getContext().getString(R.string.list_adapter_games_game_label),
                    position+1));
    }

    class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {
        private EditText scratchTotalEditText = null;
        private TextView hdcpTotalTextView = null;
        private View itemView = null;
        private TextView labelTextView = null;

        public MyViewHolder(final View itemView) {
            super(itemView);
            scratchTotalEditText = itemView.findViewById(R.id.list_game_edittext_scratch_total);
            hdcpTotalTextView = itemView.findViewById(R.id.list_game_textview_total);
            labelTextView = itemView.findViewById(R.id.list_game_textview_label);
            this.itemView = itemView;

            scratchTotalEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    int position = (Integer) itemView.getTag();
                    Game game = games.get(position);

                    try {
                        game.setValues(Short.parseShort(s.toString()));
                        setHdcpText(game, hdcpTotalTextView);
                    } catch (NumberFormatException ex) {
                        //Log.d("afterTextChanged", "NumberFormatException");
                    }

                    GamesFragment.updateTotalsText(rootView, game.getSeries());
                }
            });
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu,
                                        final View view,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu
                    //Delete menu option
                    .add(0,
                            view.getId(),
                            0,
                            view.getContext().getString(R.string.context_menu_delete))
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            removeAt(getAdapterPosition());
                            return true;
                        }
                    });
        }
    }

    public void setOnItemClickListener(onClickListener onClickListener) {
        GamesListAdapter.onClickListener = onClickListener;
    }

    public void removeAt(int position) {
        Series series = games.get(position).getSeries();
        games.get(position).delete();
        games.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, games.size());
        GamesFragment.updateTotalsText(rootView,series);
    }

    public interface onClickListener {
        void onItemClick(int position, View v);
    }

    public void setHdcpText(Game game, TextView hdcpTotalTextView) {
        hdcpTotalTextView.setText("+ " + game.getSeries().getHdcpPerGame() +
                " = " + game.getHdcpTotal());
    }
}