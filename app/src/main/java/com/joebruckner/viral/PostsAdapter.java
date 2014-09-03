package com.joebruckner.viral;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by joebruckner on 8/14/14.
 */
public class PostsAdapter extends BaseAdapter {

    List<ParseObject>    items;
    LayoutInflater       inflater;
    View.OnClickListener acceptListener;
    View.OnClickListener declineListener;

    public PostsAdapter(Activity activity, List<ParseObject> items) {
        inflater      = activity.getLayoutInflater();
        this.items    = items;
    }

    public void setAcceptListener(View.OnClickListener acceptListener) {
        this.acceptListener = acceptListener;
    }

    public void setDeclineListener(View.OnClickListener declineListener) {
        this.declineListener = declineListener;
    }

    public void removeItem(int pos) {
        Log.d("Adapter Change", "Item changing: " + pos + ". Total items: " + items.size());
        items.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ParseObject object = items.get(position);
        String className = object.getClassName();
        ViewHolder holder = new ViewHolder();
        Boolean update = true;

        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
            if(holder.sameClass(className)) update = false;
        }

        if(update) {
            int res;
            int view;
            holder.setClassName(className);

            if(holder.isPost()) {
                res  = R.layout.post_item;
                view = R.id.post_item;
            } else {
                res  = R.layout.request_item;
                view = R.id.request_item;
            }

            convertView = inflater.inflate(res, parent, false);

            holder.setItemTitle((TextView) convertView.findViewById(view));
            if(holder.isRequest()) {
                holder.setAccept((Button) convertView.findViewById(R.id.accept));
                holder.setDecline((Button) convertView.findViewById(R.id.decline));
            }
            convertView.setTag(holder);
        }

        String text;
        if(holder.isPost()) {
            text = object.getString("name") + " has a new post";
        } else {
            text = object.getString("nameFrom") + " wants to be friends";
            ButtonInfo bi = new ButtonInfo(position);
            holder.getAccept().setOnClickListener(acceptListener);
            holder.getAccept().setTag(bi);
            holder.getDecline().setOnClickListener(declineListener);
            holder.getDecline().setTag(bi);
        }
        holder.getItemTitle().setText(text);

        return convertView;
    }

    private class ViewHolder {
        TextView itemTitle = null;
        Button   accept    = null;
        Button   decline   = null;
        String   className = null;

        public ViewHolder() {
        }

        public boolean sameClass(String lastClass) {
            return className.equals(lastClass);
        }

        public boolean isPost() {
            return className.equals("Post");
        }

        public boolean isRequest() {
            return className.equals("Request");
        }

        public void setItemTitle(TextView itemTitle) {
            this.itemTitle = itemTitle;
        }

        public void setAccept(Button accept) {
            this.accept = accept;
        }

        public void setDecline(Button decline) {
            this.decline = decline;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public TextView getItemTitle() {
            return itemTitle;
        }

        public Button getAccept() {
            return accept;
        }

        public Button getDecline() {
            return decline;
        }

        public String getClassName() {
            return className;
        }
    }

    public class ButtonInfo {
        int pos;

        public ButtonInfo(int pos) {
            this.pos = pos;
        }

    }
}
