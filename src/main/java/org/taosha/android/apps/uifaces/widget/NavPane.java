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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.taosha.android.apps.uifaces.R;

/**
 * Created by san on 5/24/15.
 */
public class NavPane extends ViewGroup {
    private TextView mNameView;
    private ImageView mAvatarView;
    private ImageView mInviteView;
    private ImageView mSettingsView;
    private int mCollapsedHeight;
    private int mExpandedHeight;
    private int mThresholdHeight;
    private int mHeight;
    private RectF mCollapsedAvatarViewBounds = new RectF();
    private RectF mCollapsedNameViewBounds = new RectF();
    private RectF mCollapsedInviteViewBounds = new RectF();
    private RectF mCollapsedSettingsViewBounds = new RectF();
    private RectF mThresholdAvatarViewBounds = new RectF();
    private RectF mThresholdNameViewBounds = new RectF();
    private RectF mThresholdInviteViewBounds = new RectF();
    private RectF mThresholdSettingsViewBounds = new RectF();
    private RectF mExpandedAvatarViewBounds = new RectF();
    private RectF mExpandedNameViewBounds = new RectF();
    private RectF mExpandedInviteViewBounds = new RectF();
    private RectF mExpandedSettingsViewBounds = new RectF();
    private final float dp;
    private Animator mAnimator;
    private int heightMayCollapse;

    public NavPane(Context context) {
        this(context, null);
    }

    public NavPane(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavPane(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.NavPane);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavPane(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavPane, defStyleAttr, defStyleRes);
        mCollapsedHeight = a.getDimensionPixelSize(R.styleable.NavPane_collapsedHeight, 0);
        mExpandedHeight = a.getDimensionPixelSize(R.styleable.NavPane_expandedHeight, 0);
        mThresholdHeight = mCollapsedHeight * 2;
        mExpandedHeight = mCollapsedHeight * 4;
        mHeight = a.getBoolean(R.styleable.NavPane_collapsed, false) ? mCollapsedHeight : mExpandedHeight;
        a.recycle();

        View.inflate(getContext(), R.layout.nav_pane, this);
        mNameView = (TextView) findViewById(R.id.name);
        mAvatarView = (ImageView) findViewById(R.id.avatar);
        mInviteView = (ImageView) findViewById(R.id.action_invite);
        mSettingsView = (ImageView) findViewById(R.id.action_settings);

        dp = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), mHeight);
        mNameView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(mCollapsedHeight, MeasureSpec.EXACTLY));
        final int width = getMeasuredWidth();
        measureExpandedViewBounds(width, mExpandedHeight);
        measureThresholdViewBounds(width, mThresholdHeight);
        measureCollapsedViewBounds(width, mCollapsedHeight);

        if (mHeight >= mThresholdHeight) {
            float factor = (mHeight - mThresholdHeight) / (float) (mExpandedHeight - mThresholdHeight);
            measureFact(mAvatarView, mThresholdAvatarViewBounds, mExpandedAvatarViewBounds, factor);
        } else {
            float factor = (mHeight - mCollapsedHeight) / (float) (mThresholdHeight - mCollapsedHeight);
            measureFact(mAvatarView, mCollapsedAvatarViewBounds, mThresholdAvatarViewBounds, factor);
        }
    }

    private void measureFact(View view, RectF r0, RectF r1, float factor) {
        view.measure(
                MeasureSpec.makeMeasureSpec((int) (r0.width() + (r1.width() - r0.width()) * factor), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) (r0.height() + (r1.height() - r0.height()) * factor), MeasureSpec.EXACTLY)
        );
    }

    private void measureExpandedViewBounds(int width, int height) {
        float w, h, l, t, r;
        // name view
        w = mNameView.getMeasuredWidth();
        h = mNameView.getMeasuredHeight();
        l = (width - w) / 2;
        t = 0;
        mExpandedNameViewBounds.set(l, t, l + w, t + h);

        // avatar view
        w = h = (height - mCollapsedHeight) / 2;
        l = (width - w) / 2;
        t = mCollapsedHeight + (height - h - mCollapsedHeight) / 2;
        mExpandedAvatarViewBounds.set(l, t, l + w, t + h);

        // settings view
        w = h = 32 * dp;
        l = mExpandedAvatarViewBounds.right + 20 * dp;
        t = mExpandedAvatarViewBounds.centerY() - w / 2;
        mExpandedSettingsViewBounds.set(l, t, l + w, t + h);

        // invites view
        w = h = 32 * dp;
        r = mExpandedAvatarViewBounds.left - 20 * dp;
        t = mExpandedAvatarViewBounds.centerY() - w / 2;
        mExpandedInviteViewBounds.set(r - w, t, r, t + h);
    }

    private void measureThresholdViewBounds(int width, int height) {
        float w, h, l, t, r;
        // name view
        w = mNameView.getMeasuredWidth();
        h = mNameView.getMeasuredHeight();
        l = (width - w) / 2;
        t = 0;
        mThresholdNameViewBounds.set(l, t, l + w, t + h);

        // avatar view
        w = h = (height - mCollapsedHeight) * 0.8f;
        l = (width - w) / 2;
        t = mCollapsedHeight + (height - mCollapsedHeight - h) / 2;
        mThresholdAvatarViewBounds.set(l, t, l + w, t + h);

        // settings view
        w = h = 32 * dp;
        l = mThresholdAvatarViewBounds.right + 40 * dp;
        t = mThresholdAvatarViewBounds.centerY() - w / 2;
        mThresholdSettingsViewBounds.set(l, t, l + w, t + h);

        // invites view
        w = h = 32 * dp;
        r = mThresholdAvatarViewBounds.left - 40 * dp;
        t = mThresholdAvatarViewBounds.centerY() - w / 2;
        mThresholdInviteViewBounds.set(r - w, t, r, t + h);
    }

    private void measureCollapsedViewBounds(int width, int height) {
        float w, h, l, t, r;
        // avatar view
        w = h = 32 * dp;
        l = 16 * dp;
        t = (height - h) / 2;
        mCollapsedAvatarViewBounds.set(l, t, l + w, t + h);

        // name view
        w = mNameView.getMeasuredWidth();
        h = mNameView.getMeasuredHeight();
        l = mCollapsedAvatarViewBounds.right;
        t = (height - h) / 2;
        mCollapsedNameViewBounds.set(l, t, l + w, t + h);

        // settings view
        w = h = 32 * dp;
        r = width - 16 * dp;
        t = (height - h) / 2;
        mCollapsedSettingsViewBounds.set(r - w, t, r, t + h);

        // invite view
        w = h = 32 * dp;
        r = mCollapsedSettingsViewBounds.left - 24 * dp;
        t = (height - h) / 2;
        mCollapsedInviteViewBounds.set(r - w, t, r, t + h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mHeight >= mThresholdHeight) {
            float factor = (mHeight - mThresholdHeight) / (float) (mExpandedHeight - mThresholdHeight);
            layoutFact(mAvatarView, mThresholdAvatarViewBounds, mExpandedAvatarViewBounds, factor);
            layoutFact(mNameView, mThresholdNameViewBounds, mExpandedNameViewBounds, factor);
            layoutFact(mInviteView, mThresholdInviteViewBounds, mExpandedInviteViewBounds, factor);
            layoutFact(mSettingsView, mThresholdSettingsViewBounds, mExpandedSettingsViewBounds, factor);
        } else {
            float factor = (mHeight - mCollapsedHeight) / (float) (mThresholdHeight - mCollapsedHeight);
            layoutFact(mAvatarView, mCollapsedAvatarViewBounds, mThresholdAvatarViewBounds, factor);
            layoutFact(mNameView, mCollapsedNameViewBounds, mThresholdNameViewBounds, factor);
            layoutFact(mInviteView, mCollapsedInviteViewBounds, mThresholdInviteViewBounds, factor);
            layoutFact(mSettingsView, mCollapsedSettingsViewBounds, mThresholdSettingsViewBounds, factor);
        }
    }

    private void layoutFact(View view, RectF r0, RectF r1, float factor) {
        view.layout(
                (int) (r0.left + (r1.left - r0.left) * factor),
                (int) (r0.top + (r1.top - r0.top) * factor),
                (int) (r0.right + (r1.right - r0.right) * factor),
                (int) (r0.bottom + (r1.bottom - r0.bottom) * factor)
        );
    }

    public void setHeight(int height) {
        if (mHeight == height)
            return;

        if (height > mExpandedHeight) {
            mHeight = mExpandedHeight;
        } else if (height < mCollapsedHeight) {
            mHeight = mCollapsedHeight;
        } else {
            mHeight = height;
        }
        requestLayout();
    }

    public boolean isCollapsed() {
        return mHeight == mCollapsedHeight;
    }

    public boolean isExpanded() {
        return mHeight == mExpandedHeight;
    }

    public void expand(boolean animate) {
        if (animate) {
            animate(ObjectAnimator.ofInt(this, "height", mHeight, mThresholdHeight));
        } else {
            setHeight(mExpandedHeight);
        }
    }

    public void collapse(boolean animate) {
        if (animate) {
            animate(ObjectAnimator.ofInt(this, "height", mHeight, mCollapsedHeight));
        } else {
            setHeight(mCollapsedHeight);
        }
    }

    private void animate(Animator animator) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = animator;
        mAnimator.start();
    }

    public boolean isAnimating() {
        return mAnimator != null && mAnimator.isRunning();
    }

    public int getThresholdHeight() {
        return mThresholdHeight;
    }

    public int getCollapsedHeight() {
        return mCollapsedHeight;
    }
}
