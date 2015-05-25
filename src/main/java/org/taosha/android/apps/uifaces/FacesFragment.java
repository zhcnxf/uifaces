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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import org.taosha.android.apps.uifaces.widget.NavPane;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;


/**
 * Created by san on 5/23/15.
 */
public class FacesFragment extends Fragment implements StreamFragment.TouchIntervene {

    @InjectView(R.id.navPane)
    NavPane mNavPane;

    @InjectView(R.id.tabs)
    RadioGroup mRadioGroup;

    @Icicle
    int mCheckedRadioButtonId = R.id.everybody;

    private int mOrigHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faces, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.everybody) {
                    onShowCategory(getString(R.string.everybody));
                } else if (checkedId == R.id.authorized) {
                    onShowCategory(getString(R.string.authorized));
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        mRadioGroup.check(mCheckedRadioButtonId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void onShowCategory(String category) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container, StreamFragment.newInstance(category))
                .commit();
    }

    @Override
    public void start() {
        mOrigHeight = mNavPane.getHeight();
    }

    @Override
    public boolean onDrag(float distance, float delta) {
        if (mNavPane.isAnimating())
            return true;

        int height = Math.round(mOrigHeight + distance);

        if (delta > 0 && mNavPane.isCollapsed()) {
            mNavPane.expand(true);
            return true;
        }

        if (!mNavPane.isCollapsed() && delta < 0 && height <= mNavPane.getThresholdHeight()) {
            mNavPane.collapse(true);
            return true;
        }

        mNavPane.setHeight(height);

        return (delta > 0 && !mNavPane.isExpanded()) || (delta < 0 && !mNavPane.isCollapsed());
    }
}
