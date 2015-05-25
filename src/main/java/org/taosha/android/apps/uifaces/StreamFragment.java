/*
 * Copyright 2015 Taosha Organization
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.taosha.android.apps.uifaces;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.taosha.android.apps.uifaces.model.Face;
import org.taosha.android.apps.uifaces.widget.UfRecyclerView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by san on 5/23/15.
 */
public class StreamFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    @InjectView(R.id.recycler)
    UfRecyclerView mRecyclerView;

    private TouchIntervene mTouchIntervene;

    private UfRecyclerView.TouchDispatcher mTouchDispatcher = new UfRecyclerView.TouchDispatcher() {
        private int[] firstPos;
        private float origY, prevY;

        @Override
        public boolean dispatchTouchEvent(UfRecyclerView rv, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.prevY = this.origY = e.getY();
                    mTouchIntervene.start();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float distance = e.getY() - origY, deltaY = e.getY() - prevY;
                    prevY = e.getY();
                    return mTouchIntervene.onDrag(distance, deltaY);
            }

            return false;
        }

        private boolean isPositionTop(RecyclerView rv) {
            if (rv.getChildCount() == 0)
                return true;

            final StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) rv.getLayoutManager();
            if (firstPos == null) {
                firstPos = new int[lm.getSpanCount()];
            }
            lm.findFirstCompletelyVisibleItemPositions(firstPos);
            for (int p : firstPos) {
                if (p == 0)
                    return true;
            }
            return false;
        }
    };

    public static Fragment newInstance(String category) {
        StreamFragment fragment = new StreamFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getParentFragment() instanceof TouchIntervene) {
            mTouchIntervene = (TouchIntervene) getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTouchIntervene = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        int columnCount = adjustColumnCount();
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setTouchDispatcher(mTouchDispatcher);
    }

    private int adjustColumnCount() {
        final float idealCardWidth = getResources().getDimension(R.dimen.ideal_card_width);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        return Math.round(metrics.widthPixels / idealCardWidth);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setAdapter(new FaceAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public interface TouchIntervene {
        void start();

        /**
         * @param distance y-distance from touch down
         * @param delta    y-distance from last motion event
         * @return true if drag event is consumed
         */
        boolean onDrag(float distance, float delta);
    }

    static class FaceViewHolder extends RecyclerView.ViewHolder {
        public FaceViewHolder(View itemView) {
            super(itemView);
        }

        public void setItem(Face item) {
        }
    }

    class FaceAdapter extends RecyclerView.Adapter<FaceViewHolder> {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        @Override
        public FaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.card_face, parent, false);
            return new FaceViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FaceViewHolder holder, int position) {
            final StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            lp.setFullSpan(position == 2);
            holder.setItem(getItem(position));
        }

        private Face getItem(int position) {
            return null;
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}