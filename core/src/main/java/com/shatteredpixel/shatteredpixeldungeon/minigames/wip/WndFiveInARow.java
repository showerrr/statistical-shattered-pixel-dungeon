package com.shatteredpixel.shatteredpixeldungeon.minigames.wip;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class WndFiveInARow extends Window {

    public int[][] map = new int[15][15];

    public enum Color {
        BLACK,
        WHITE,
        NONE
    }

    public static Color color = Color.NONE;
    public Color turn = Color.BLACK;

    public Select select;

    public WndFiveInARow() {

        try {
            FIRClient.sendMsg = "Hello";
            FIRClient.createThread();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ColorBlock colorBlock = new ColorBlock(145,145,0xFFD0B557);
        colorBlock.x = 0;
        colorBlock.y = 0;
        add(colorBlock);

        for (int i = 0;i < 16;i++) {
            ColorBlock colorBlock1 = new ColorBlock(135,1,0xFF000000);
            colorBlock1.x = 4;
            colorBlock1.y = 4 + 9 * i;
            add(colorBlock1);

            ColorBlock colorBlock2 = new ColorBlock(1,135,0xFF000000);
            colorBlock2.x = 4 + 9 * i;
            colorBlock2.y = 4;
            add(colorBlock2);
        }

        for (int i = 0;i < 16;i++) {
            for (int j = 0;j < 16;j++) {
                Grid grid = new Grid();
                grid.setRect(i * 9,j * 9,9,9);
                add(grid);
            }
        }

        select = new Select();
        select.x = 145;
        select.y = 145;
        select.visible = false;
        add(select);

        resize(144,144);
    }

    public class Grid extends IconButton {

        @Override
        public void onClick() {
            if (icon != null || color == Color.NONE || color != turn) {
                return;
            }

//            FIRClient.sendMsg = "end";

            if (select.visible) {
                if (select.x == x && select.y == y) {
                    Image image;

                    if (color == Color.BLACK) {
                        image = new BlackChess();
                        color = Color.WHITE;
                        turn = Color.WHITE;
                    } else {
                        image = new WhiteChess();
                        color = Color.BLACK;
                        turn = Color.BLACK;
                    }

                    icon(image);

                }
                select.visible = false;
                return;
            }

            select.x = x;
            select.y = y;
            select.visible = true;

        }
    }

    public class WhiteChess extends Image {

        public WhiteChess() {
            super(Assets.Interfaces.WHITE_CHESS);
            scale.set(0.5f);
        }
    }

    public class BlackChess extends Image {

        public BlackChess() {
            super(Assets.Interfaces.BLACK_CHESS);
            scale.set(0.5f);
        }
    }

    public class Select extends Image {

        public Select() {
            super(Assets.Interfaces.TARGET);
        }
    }

    public static class FIRClient {

        private static Thread t1,t2;
        private static Socket s;

        public static String sendMsg = "";
        public static String getMsg = "";

        public static void createThread() throws IOException {
            s = new Socket("101.43.49.222",30000);

            t1 = new sendMessage(s);
            t2 = new getMessage(s);
            t1.setName("Client Thread-Send");
            t2.setName("Client Thread-Receive");
            t1.start();
            t2.start();
        }

        public static void main(String[] args) throws IOException {
            createThread();
        }

        public static class getMessage extends Thread {

            private  Socket socket;
            public getMessage(Socket s) {
                socket = s;
            }

            @Override public void run(){
                try {
                    BufferedReader br;
                    while (true) {
                        br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
                        while ((getMsg = br.readLine()) != null) {

                            if (getMsg.contains("Black")) {
                                WndFiveInARow.color = Color.BLACK;
                                GLog.i("创建房间成功！你选择了黑棋");
                                GameScene.show(new WndFiveInARow());
                            }
                            if (getMsg.contains("WHITE")) {
                                WndFiveInARow.color = Color.WHITE;
                                GLog.i("创建房间成功！你选择了白棋");
                                GameScene.show(new WndFiveInARow());
                            }

                            if (getMsg.contains("Failed to create room.")) {
                                GLog.i("创建房间失败！请检查网络或者换个房间号");
                            }

//                            GLog.i(getMsg);
//                    System.out.println(Thread.currentThread().getName()+"message:"+msg);
                        }
                        br.close();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            public Socket getSocket() {
                return socket;
            }
        }

        public static class sendMessage extends Thread {

            private Socket socket;
            public sendMessage(Socket s) {
                socket = s;
            }

            @Override public void run(){

                try {
                    while (true) {
                        if (sendMsg != null) {
                            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
                            pw.println(sendMsg);
                            pw.flush();
                            sendMsg = null;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
