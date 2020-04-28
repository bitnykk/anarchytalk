/*
 * ChatMessageAdapter.java
 *
 *************************************************************************
 * Copyright 2010 Christofer Engel
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
package com.rubika.aotalk;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatMessageAdapter extends BaseAdapter {
    private Context context;
    private List<ChatMessage> listMessages;

    public ChatMessageAdapter(Context context, List<ChatMessage> listMessages) {
        this.context = context;
        this.listMessages = listMessages;
    }

    public int getCount() {
        return listMessages.size();
    }

    public Object getItem(int position) {
        return listMessages.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ChatMessage entry = listMessages.get(position);
        
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message, null);
        }

        TextView message = (TextView) convertView.findViewById(R.id.message);
        
		String color = "#FFFFFF";
        
		switch(entry.getType()) {
			case ChatParser.TYPE_SYSTEM_MESSAGE:
				color = "#FFCC33";
				break;
			case ChatParser.TYPE_PRIVATE_MESSAGE:
				color = "#88FF88";
				break;
			case ChatParser.TYPE_CLIENT_MESSAGE:
				color = "#CC99CC";
				break;
			case ChatParser.TYPE_GROUP_MESSAGE:
				color = "#FFFFFF";
				break;
		}
        
       	message.setTextColor(Color.parseColor(color));
        
        message.setText("");
//      message.append(Html.fromHtml(entry.getMessage())); // desactivated ...
// ... was removing all ao links inner contents hence reformattings below ...
        String htmlString = entry.getMessage().toString(); // import raw datas
//      message.append(htmlString+"\n"); // initial DEBUG
        String fontRegul = "<font (?:[^>]+)>(.+?)</font>"; // eliminate fonts
        String nofontString = htmlString; // create filtered
        nofontString = nofontString.replaceAll(fontRegul, "$1"); // apply filter
//        nofontString = nofontString.replaceAll(fontRegul, "$1"); // second pass
        String hrefRegul = "<a href=[/\\\\]{0,2}[\"|'](?:text|chatcmd):[/\\\\]{2,3}(.+?)[/\\\\]{0,2}[\"|']>(.+?)</a>"; // manage links
        String clearString = nofontString; // instance exploded
        clearString = clearString.replaceAll(hrefRegul, "<br />$2 [$1]<br />"); // apply explosion
//        clearString = clearString.replaceAll(hrefRegul, "<br />$2 [$1]<br />"); // second pass
//       message.append(clearString+"\n"); // reformat DEBUG
        String textConv = Html.fromHtml(clearString).toString();
        textConv = textConv.replaceAll("\">", ""); // final cleanup        
        message.append(textConv); // final       
// ... end of ao specific links reformat
        message.setMovementMethod(LinkMovementMethod.getInstance());
                
        return convertView;
    }
}
