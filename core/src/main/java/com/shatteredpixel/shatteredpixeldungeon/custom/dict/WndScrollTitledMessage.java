package com.shatteredpixel.shatteredpixeldungeon.custom.dict;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndScrollTitledMessage extends Window {

    protected static final int WIDTH_MIN    = 120;
    protected static final int WIDTH_MAX    = 220;
    protected static final int GAP	= 2;

    public WndScrollTitledMessage(Image icon, String title, String message, float maxHeight ) {

        this( new IconTitle( icon, title ), message, maxHeight );

    }

    public WndScrollTitledMessage(Component titlebar, String message, float maxHeight ) {

        super();

        int width = WIDTH_MIN;


        ScrollPane pane = new ScrollPane(new Component());
        Component content = pane.content();
        add(pane);


        content.add(titlebar);
        titlebar.setRect( 0, 0, width, 0 );

        RenderedTextBlock text = PixelScene.renderTextBlock( 6 );
        text.text( message, width );
        content.add( text );
        text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );

        content.setSize(width,text.bottom()+2);
        resize( width, Math.min((int)maxHeight+1, (int)content.height() +1));
        content.setPos(0,0);
        pane.setRect(0,0,width,(int)maxHeight+1);


    }
}
