package com.example.thenotoriousrog.tornadomusicplayer;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.thenotoriousrog.tornadomusicplayer.R;;
import com.woxthebox.draglistview.DragItem;


/**
 * Created by thenotoriousrog on 6/26/17.
 */

public class DragSongItem extends DragItem {

    public DragSongItem(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void onBindDragView(View clickedView, View dragView) {
        CharSequence text = ((TextView) clickedView.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.text)).getText();
        ((TextView) dragView.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.text)).setText(text);
        CardView dragCard = ((CardView) dragView.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.card));
        CardView clickedCard = ((CardView) clickedView.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.card));

        dragCard.setMaxCardElevation(40);
        dragCard.setCardElevation(clickedCard.getCardElevation());
        // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23

       // dragView.setBackgroundColor(Color.GRAY);
        //dragCard.setForeground(clickedView.getResources().getDrawable(R.drawable.card_view_drag_foreground)); // this needs to be resolved some other way. no item right now for this.
    }

    @Override
    public void onMeasureDragView(View clickedView, View dragView) {
        CardView dragCard = ((CardView) dragView.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.card));
        CardView clickedCard = ((CardView) clickedView.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.card));
        int widthDiff = dragCard.getPaddingLeft() - clickedCard.getPaddingLeft() + dragCard.getPaddingRight() -
                clickedCard.getPaddingRight();
        int heightDiff = dragCard.getPaddingTop() - clickedCard.getPaddingTop() + dragCard.getPaddingBottom() -
                clickedCard.getPaddingBottom();
        int width = clickedView.getMeasuredWidth() + widthDiff;
        int height = clickedView.getMeasuredHeight() + heightDiff;
        dragView.setLayoutParams(new FrameLayout.LayoutParams(width, height));

        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        dragView.measure(widthSpec, heightSpec);
    }

    @Override
    public void onStartDragAnimation(View dragView) {
        CardView dragCard = ((CardView) dragView.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.card));
        ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 40);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(ANIMATION_DURATION - 200); // try to make animation quicker.
        anim.start();
    }

    @Override
    public void onEndDragAnimation(View dragView) {
        CardView dragCard = ((CardView) dragView.findViewById(R.id.card));
        ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 6);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(ANIMATION_DURATION - 200); // try to make animation quicker.
        anim.start();
    }
}
