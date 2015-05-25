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

package org.taosha.android.apps.uifaces.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by san on 5/25/15.
 */
public class UfRecyclerView extends RecyclerView {

    private TouchDispatcher mTouchDispatcher;

    public interface TouchDispatcher {
        boolean dispatchTouchEvent(UfRecyclerView ufRecyclerView, MotionEvent ev);
    }

    public UfRecyclerView(Context context) {
        super(context);
    }

    public UfRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UfRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setmTouchDispatcher(TouchDispatcher mTouchDispatcher) {
        this.mTouchDispatcher = mTouchDispatcher;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mTouchDispatcher == null)
            return super.dispatchTouchEvent(ev);

        return mTouchDispatcher.dispatchTouchEvent(this, ev) || super.dispatchTouchEvent(ev);
    }
}
