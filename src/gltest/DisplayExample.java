package gltest;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.Sys;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class DisplayExample {
	int width = 1000;
	int height = 800;
	
	/** time at last frame */
	long lastFrame;
	
	/** frames per second */
	int fps;
	/** last fps time */
	long lastFPS;
	boolean start=false;
	Board board=new Board();
	//Player p=new Player();
	Texture tex,tmptex,numbers,score,newgame,highscore,test,alphabet,enter;
	float ngr=1;
	float ngg=1;
	float ngb=1;
	
	float hsr=1;
	float hsg=1;
	float hsb=1;
	HighScore hs=null;
	boolean[] mouseOver=new boolean[10];
	boolean prompt=false;
	boolean lock = false;
	char[] initials=new char[3];
	int count=-1;
	
	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try{
			fis = new FileInputStream("save/hs.dat");
			in = new ObjectInputStream(fis);
			hs = (HighScore)in.readObject();
			in.close();
		}catch(IOException ex){
			System.out.println("No saved HS.");
			hs=new HighScore();

			FileOutputStream fos = null;
			ObjectOutputStream out = null;
			try{
				fos = new FileOutputStream("save/hs.dat");
				out = new ObjectOutputStream(fos);
				out.writeObject(hs);
				out.close();
			}catch(IOException e){
				System.out.println("something");
				e.printStackTrace();
			}
		}catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
		
		initials[0]='[';
		initials[1]='[';
		initials[2]='[';
		
		hs.printHS();
		board.shuffle(100);
		initGL(); // init OpenGL
		getDelta(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer
		init();
		
		while (!Display.isCloseRequested()) {
			int delta = getDelta();
			
			update(delta);
			
			renderGL();
			
			Display.update();
			Display.sync(60); // cap fps to 60fps
		}

		Display.destroy();
	}
	
	public void init(){
		Display.setTitle("Iowa");
		try {
			tex= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/hrestiles.png"));
			System.out.println("Texture loaded: "+tex);
			System.out.println(">> Image width: "+tex.getImageWidth());
			System.out.println(">> Image height: "+tex.getImageHeight());
			System.out.println(">> Texture width: "+tex.getTextureWidth());
			System.out.println(">> Texture height: "+tex.getTextureHeight());
			System.out.println(">> Texture ID: "+tex.getTextureID());
			
			tmptex= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/tmp.png"));
			System.out.println("Texture loaded: "+tmptex);
			System.out.println(">> Image width: "+tmptex.getImageWidth());
			System.out.println(">> Image height: "+tmptex.getImageHeight());
			System.out.println(">> Texture width: "+tmptex.getTextureWidth());
			System.out.println(">> Texture height: "+tmptex.getTextureHeight());
			System.out.println(">> Texture ID: "+tmptex.getTextureID());
			
			numbers= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/numbers.png"));
			System.out.println("Texture loaded: "+numbers);
			System.out.println(">> Image width: "+numbers.getImageWidth());
			System.out.println(">> Image height: "+numbers.getImageHeight());
			System.out.println(">> Texture width: "+numbers.getTextureWidth());
			System.out.println(">> Texture height: "+numbers.getTextureHeight());
			System.out.println(">> Texture ID: "+numbers.getTextureID());
			
			score= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/score.png"));
			System.out.println("Texture loaded: "+score);
			System.out.println(">> Image width: "+score.getImageWidth());
			System.out.println(">> Image height: "+score.getImageHeight());
			System.out.println(">> Texture width: "+score.getTextureWidth());
			System.out.println(">> Texture height: "+score.getTextureHeight());
			System.out.println(">> Texture ID: "+score.getTextureID());
			
			newgame= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/new.png"));
			System.out.println("Texture loaded: "+newgame);
			System.out.println(">> Image width: "+newgame.getImageWidth());
			System.out.println(">> Image height: "+newgame.getImageHeight());
			System.out.println(">> Texture width: "+newgame.getTextureWidth());
			System.out.println(">> Texture height: "+newgame.getTextureHeight());
			System.out.println(">> Texture ID: "+newgame.getTextureID());
			
			highscore= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/hs.png"));
			System.out.println("Texture loaded: "+highscore);
			System.out.println(">> Image width: "+highscore.getImageWidth());
			System.out.println(">> Image height: "+highscore.getImageHeight());
			System.out.println(">> Texture width: "+highscore.getTextureWidth());
			System.out.println(">> Texture height: "+highscore.getTextureHeight());
			System.out.println(">> Texture ID: "+highscore.getTextureID());
			
			test= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/test.png"));
			System.out.println("Texture loaded: "+test);
			System.out.println(">> Image width: "+test.getImageWidth());
			System.out.println(">> Image height: "+test.getImageHeight());
			System.out.println(">> Texture width: "+test.getTextureWidth());
			System.out.println(">> Texture height: "+test.getTextureHeight());
			System.out.println(">> Texture ID: "+test.getTextureID());
			
			alphabet= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/alphabet.png"));
			System.out.println("Texture loaded: "+alphabet);
			System.out.println(">> Image width: "+alphabet.getImageWidth());
			System.out.println(">> Image height: "+alphabet.getImageHeight());
			System.out.println(">> Texture width: "+alphabet.getTextureWidth());
			System.out.println(">> Texture height: "+alphabet.getTextureHeight());
			System.out.println(">> Texture ID: "+alphabet.getTextureID());
			
			enter= TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/enter.png"));
			System.out.println("Texture loaded: "+enter);
			System.out.println(">> Image width: "+enter.getImageWidth());
			System.out.println(">> Image height: "+enter.getImageHeight());
			System.out.println(">> Texture width: "+enter.getTextureWidth());
			System.out.println(">> Texture height: "+enter.getTextureHeight());
			System.out.println(">> Texture ID: "+enter.getTextureID());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void update(int delta) {
		// rotate quad
		//rotation += 0.15f * delta;
		if (!lock){
			

			int mX = Mouse.getX();
			int mY = Mouse.getY();

			//new game - change color is mouse over, shuffle board if click
			if ((mX>800 && mX<1000) && (mY>500 && mY<600)) {		
				ngr=0;
				ngg=0;
				ngb=1;
				if (Mouse.isButtonDown(0)){
					board = new Board();
					board.shuffle(100);

				}
			} else {
				ngr=1;
				ngg=1;
				ngb=1;
			}
			
			//High Score - change color if mouse over
			if ((mX>800 && mX<1000) && (mY>400 && mY<500)) {		
				hsr=1;
				hsg=1;
				hsb=0;
				if (Mouse.isButtonDown(0)){

				}
			} else {
				hsr=1;
				hsg=1;
				hsb=1;
			}

			//scores - change color if mouse over
			if (mX>840 && mX<1000) {		
				if (mY>360 && mY<400){
					mouseOver[0]=true;
				} else {
					mouseOver[0]=false;
				}
				if (mY>320 && mY<360){
					mouseOver[1]=true;
				} else{
					mouseOver[1]=false;
				}
				if (mY>280 && mY<320){
					mouseOver[2]=true;
				} else {
					mouseOver[2]=false;
				}
				if (mY>240 && mY<280){
					mouseOver[3]=true;
				} else {
					mouseOver[3]=false;
				}
				if (mY>200 && mY<240){
					mouseOver[4]=true;
				} else {
					mouseOver[4]=false;
				}
				if (mY>160 && mY<200){
					mouseOver[5]=true;
				} else { 
					mouseOver[5]=false;
				}
				if (mY>120 && mY<160){
					mouseOver[6]=true;
				} else{
					mouseOver[6]=false;
				}
				if (mY>80 && mY<120){
					mouseOver[7]=true;
				} else {
					mouseOver[7]=false;
				}
				if (mY>40 && mY<80){
					mouseOver[8]=true;
				} else {
					mouseOver[8]=false;
				}
				if (mY>0 && mY<40){
					mouseOver[9]=true;
				} else {
					mouseOver[9]=false;
				}
			} else {
				for (int i=0;i<10;i++){
					mouseOver[i]=false;
				}
			}
			
			if (Mouse.isButtonDown(0)) {
				int X = Mouse.getX();
				int Y = Mouse.getY();

				//get pos & swap
				System.out.println(board.getBoardPos(X,Y));
				board.swap(board.getBoardPos(X,Y), board.current);
				//p.incMoves();
				if (board.checkWin()){
					prompt=true;
					lock=true;
					count=0;
				} else {
					prompt=false;
				}
			}
		}
		Keyboard.enableRepeatEvents(true);
		if (count<3 && count>=0){
			char t;
			while(Keyboard.next()){
				t=Keyboard.getEventCharacter();
				if (t>=65 && t<=90){
					initials[count]=t;
				} else if (t>=97 && t<=122){
					initials[count]=(char)(t-32);
				}
				if (!Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_RETURN){
						count++;
					}
				}
			}
		} else if (count>=3){
			prompt=false;
			hs.addScore(board.count, initials);
			lock=false;
			count=-1;
			initials[0]='[';
			initials[1]='[';
			initials[2]='[';
			
			FileOutputStream fos = null;
			ObjectOutputStream out = null;
			try{
				fos = new FileOutputStream("save/hs.dat");
				out = new ObjectOutputStream(fos);
				out.writeObject(hs);
				out.close();
			}catch(IOException e){
				System.out.println("something");
				e.printStackTrace();
			}
		}
		
		//updateFPS(); // update FPS Counter
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
	 
	    return delta;
	}
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	public void initGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, 0, height, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void renderGL() {
		int j;
		// Clear The Screen And The Depth Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// R,G,B,A Set The Color To Blue One Time Only
		GL11.glColor3f(0.5f, 0.5f, 1.0f);

		// draw quad
		GL11.glPushMatrix();
			tex.bind();
			GL11.glBegin(GL11.GL_QUADS); //change this to use height & width
				for (int i=0;i<16;i++){
					j=board.b[i];
					if (j!=15){
						GL11.glColor3f(board.red, board.green, board.blue);
						//0-1
						GL11.glTexCoord2f((float)((tex.getImageWidth()/4)*(j%4))/(float)tex.getTextureWidth(), ((float)(tex.getImageHeight()/4)/(float)tex.getTextureHeight())+(float)((tex.getImageHeight()/4)*(j/4))/(float)tex.getTextureHeight());
						GL11.glVertex2i(0+((i%4)*200),600-((i/4)*200) );
						//1-1
						GL11.glTexCoord2f(((float)(tex.getImageWidth()/4)/(float)tex.getTextureWidth())+(float)((tex.getImageWidth()/4)*(j%4))/(float)tex.getTextureWidth(), ((float)(tex.getImageHeight()/4)/(float)tex.getTextureHeight())+(float)((tex.getImageHeight()/4)*(j/4))/(float)tex.getTextureHeight());
						GL11.glVertex2i(200+((i%4)*200),600-((i/4)*200) );
						//1-0
						GL11.glTexCoord2f(((float)(tex.getImageWidth()/4)/(float)tex.getTextureWidth())+(float)((tex.getImageWidth()/4)*(j%4))/(float)tex.getTextureWidth(), (float)((tex.getImageHeight()/4)*(j/4))/(float)tex.getTextureHeight());
						GL11.glVertex2i(200+((i%4)*200),800-((i/4)*200) );
						//0-0
						GL11.glTexCoord2f((float)((tex.getImageWidth()/4)*(j%4))/(float)tex.getTextureWidth(), (float)((tex.getImageHeight()/4)*(j/4))/(float)tex.getTextureHeight());
						GL11.glVertex2i(0+((i%4)*200),800-((i/4)*200) );
					}
				}
			GL11.glEnd();
			

			
			
			numbers.bind();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glColor3f(1, 1, 1);
				
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(board.count/100))/(float)numbers.getTextureWidth(), 0);
				GL11.glVertex2f(800, 700);
				
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(board.count/100))/(float)numbers.getTextureWidth(), 1);
				GL11.glVertex2f(800, 600);
				
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((board.count/100)+1))/(float)numbers.getTextureWidth(), 1);
				GL11.glVertex2f(800+(200/3),600);
				
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((board.count/100)+1))/(float)numbers.getTextureWidth(), 0);
				GL11.glVertex2f(800+(200/3),700);
			GL11.glEnd();	
				
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((board.count%100)/10))/(float)numbers.getTextureWidth(), 1);
				GL11.glVertex2f(800+(200/3),600);
			
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((board.count%100)/10))/(float)numbers.getTextureWidth(), 0);
				GL11.glVertex2f(800+(200/3),700);
			
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(((board.count%100)/10)+1))/(float)numbers.getTextureWidth(), 0);
				GL11.glVertex2f(800+((200/3)*2),700);
			
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(((board.count%100)/10)+1))/(float)numbers.getTextureWidth(), 1);
				GL11.glVertex2f(800+((200/3)*2),600);
			GL11.glEnd();
			
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(board.count%10))/(float)numbers.getTextureWidth(), 0);
				GL11.glVertex2f(800+((200/3)*2),700);
				
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(board.count%10))/(float)numbers.getTextureWidth(), 1);
				GL11.glVertex2f(800+((200/3)*2),600);
				
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((board.count%10)+1))/(float)numbers.getTextureWidth(), 1);
				GL11.glVertex2f(1000, 600);
				
				GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((board.count%10)+1))/(float)numbers.getTextureWidth(), 0);
				GL11.glVertex2f(1000, 700);
			GL11.glEnd();
			
			score.bind();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(800,800);
				GL11.glTexCoord2f((float)score.getImageWidth()/(float)score.getTextureWidth(), 0);
				GL11.glVertex2f(1000,800);
				GL11.glTexCoord2f((float)score.getImageWidth()/(float)score.getTextureWidth(), 1);
				GL11.glVertex2f(1000,700);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex2f(800,700);
				
			GL11.glEnd();
			
			
			
			newgame.bind();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glColor3f(ngr, ngg, ngb);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(800,600);
				GL11.glTexCoord2f((float)newgame.getImageWidth()/(float)newgame.getTextureWidth(), 0);
				GL11.glVertex2f(1000,600);
				GL11.glTexCoord2f((float)newgame.getImageWidth()/(float)newgame.getTextureWidth(), 1);
				GL11.glVertex2f(1000,500);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex2f(800,500);
				
			GL11.glEnd();
			
			
			highscore.bind();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glColor3f(hsr, hsg, hsb);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(800,500);
				GL11.glTexCoord2f((float)highscore.getImageWidth()/(float)highscore.getTextureWidth(), 0);
				GL11.glVertex2f(1000,500);
				GL11.glTexCoord2f((float)highscore.getImageWidth()/(float)highscore.getTextureWidth(), 1);
				GL11.glVertex2f(1000,400);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex2f(800,400);
				
			
			GL11.glEnd();
			
			
			numbers.bind();
			GL11.glBegin(GL11.GL_QUADS);
				for(int i=0;i<10;i++){
					GL11.glColor3f((float)i/(float)10, 1-(float)i/(float)10, 0);
					GL11.glTexCoord2f((((float)numbers.getImageWidth()/10)*i)/(float)numbers.getTextureWidth(), 0);
					GL11.glVertex2f(800, 400-(i*40));
					GL11.glTexCoord2f((((float)numbers.getImageWidth()/10)*(i+1))/(float)numbers.getTextureWidth(), 0);
					GL11.glVertex2f(840, 400-(i*40));
					GL11.glTexCoord2f((((float)numbers.getImageWidth()/10)*(i+1))/(float)numbers.getTextureWidth(), 1);
					GL11.glVertex2f(840, 360-(i*40));
					GL11.glTexCoord2f((((float)numbers.getImageWidth()/10)*i)/(float)numbers.getTextureWidth(), 1);
					GL11.glVertex2f(800, 360-(i*40));
				}
			
			GL11.glEnd();
			

			

			
			numbers.bind();
			GL11.glBegin(GL11.GL_QUADS);
			
				for (int i=0;i<10;i++){
					GL11.glColor3f((float)i/(float)10, 1-(float)i/(float)10, 0);

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(hs.getScore(i)/100))/(float)numbers.getTextureWidth(), 0);
					GL11.glVertex2f(880, 400-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(hs.getScore(i)/100))/(float)numbers.getTextureWidth(), 1);
					GL11.glVertex2f(880,360-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((hs.getScore(i)/100)+1))/(float)numbers.getTextureWidth(), 1);
					GL11.glVertex2f(880+40,360-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((hs.getScore(i)/100)+1))/(float)numbers.getTextureWidth(), 0);
					GL11.glVertex2f(880+40,400-(i*40));




					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((hs.getScore(i)%100)/10))/(float)numbers.getTextureWidth(), 1);
					GL11.glVertex2f(880+40,360-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((hs.getScore(i)%100)/10))/(float)numbers.getTextureWidth(), 0);
					GL11.glVertex2f(880+40,400-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(((hs.getScore(i)%100)/10)+1))/(float)numbers.getTextureWidth(), 0);
					GL11.glVertex2f(880+80,400-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(((hs.getScore(i)%100)/10)+1))/(float)numbers.getTextureWidth(), 1);
					GL11.glVertex2f(880+80,360-(i*40));




					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(hs.getScore(i)%10))/(float)numbers.getTextureWidth(), 0);
					GL11.glVertex2f(880+80,400-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)(hs.getScore(i)%10))/(float)numbers.getTextureWidth(), 1);
					GL11.glVertex2f(880+80,360-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((hs.getScore(i)%10)+1))/(float)numbers.getTextureWidth(), 1);
					GL11.glVertex2f(880+120, 360-(i*40));

					GL11.glTexCoord2f((((float)numbers.getImageWidth()/(float)10)*(float)((hs.getScore(i)%10)+1))/(float)numbers.getTextureWidth(), 0);
					GL11.glVertex2f(880+120, 400-(i*40));
				}
			GL11.glEnd();
			
			
			
			alphabet.bind();
			GL11.glBegin(GL11.GL_QUADS);
			
				for (int i=0;i<10;i++){
					char[] tmp=hs.getName(i);
					GL11.glColor3f((float)i/(float)10, 1-(float)i/(float)10, 0);

					if (mouseOver[i]){
						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)((tmp[0])-65))/(float)alphabet.getTextureWidth(), 0);
						GL11.glVertex2f(880, 400-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)((tmp[0])-65))/(float)alphabet.getTextureWidth(), 1);
						GL11.glVertex2f(880,360-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)(((tmp[0])-65)+1))/(float)alphabet.getTextureWidth(), 1);
						GL11.glVertex2f(880+40,360-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)(((tmp[0])-65)+1))/(float)alphabet.getTextureWidth(), 0);
						GL11.glVertex2f(880+40,400-(i*40));




						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)((tmp[1])-65))/(float)alphabet.getTextureWidth(), 1);
						GL11.glVertex2f(880+40,360-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)((tmp[1])-65))/(float)alphabet.getTextureWidth(), 0);
						GL11.glVertex2f(880+40,400-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)(((tmp[1])-65)+1))/(float)alphabet.getTextureWidth(), 0);
						GL11.glVertex2f(880+80,400-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)(((tmp[1])-65)+1))/(float)alphabet.getTextureWidth(), 1);
						GL11.glVertex2f(880+80,360-(i*40));




						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)((tmp[2])-65))/(float)alphabet.getTextureWidth(), 0);
						GL11.glVertex2f(880+80,400-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)((tmp[2])-65))/(float)alphabet.getTextureWidth(), 1);
						GL11.glVertex2f(880+80,360-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)(((tmp[2])-65)+1))/(float)alphabet.getTextureWidth(), 1);
						GL11.glVertex2f(880+120, 360-(i*40));

						GL11.glTexCoord2f((((float)alphabet.getImageWidth()/(float)27)*(float)(((tmp[2])-65)+1))/(float)alphabet.getTextureWidth(), 0);
						GL11.glVertex2f(880+120, 400-(i*40));
					}
				}
			GL11.glEnd();
			
			
			if (prompt){
				
				//800X500
				enter.bind();
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glColor3f(1, 1, 1);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2f(100, 650);
					GL11.glTexCoord2f(0, (float)enter.getImageHeight()/(float)enter.getTextureHeight());
					GL11.glVertex2f(100, 150);
					GL11.glTexCoord2f((float)enter.getImageWidth()/(float)enter.getTextureWidth(), (float)enter.getImageHeight()/(float)enter.getTextureHeight());
					GL11.glVertex2f(900, 150);
					GL11.glTexCoord2f((float)enter.getImageWidth()/(float)enter.getTextureWidth(), 0);
					GL11.glVertex2f(900, 650);
				GL11.glEnd();
				
				alphabet.bind();
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glColor3f(1, 1, 1);
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)((initials[0])-65)/(float)alphabet.getTextureWidth(), 0);
					GL11.glVertex2f((float)100 + ((((float)8/(float)88)*(float)800)*(float)3) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500) + (((float)4/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)((initials[0])-65)/(float)alphabet.getTextureWidth(), 1);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)3) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)(((initials[0])-65)+1)/(float)alphabet.getTextureWidth(), 1);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)4) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)(((initials[0])-65)+1)/(float)alphabet.getTextureWidth(), 0);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)4) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500) + (((float)4/(float)16)*(float)500));
				GL11.glEnd();
				
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glColor3f(1, 1, 1);
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)((initials[1])-65)/(float)alphabet.getTextureWidth(), 0);
					GL11.glVertex2f((float)100 + ((((float)8/(float)88)*(float)800)*(float)4) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500) + (((float)4/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)((initials[1])-65)/(float)alphabet.getTextureWidth(), 1);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)4) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)(((initials[1])-65)+1)/(float)alphabet.getTextureWidth(), 1);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)5) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)(((initials[1])-65)+1)/(float)alphabet.getTextureWidth(), 0);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)5) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500) + (((float)4/(float)16)*(float)500));
				GL11.glEnd();
			
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glColor3f(1, 1, 1);
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)((initials[2])-65)/(float)alphabet.getTextureWidth(), 0);
					GL11.glVertex2f((float)100 + ((((float)8/(float)88)*(float)800)*(float)5) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500) + (((float)4/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)((initials[2])-65)/(float)alphabet.getTextureWidth(), 1);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)5) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)(((initials[2])-65)+1)/(float)alphabet.getTextureWidth(), 1);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)6) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500));
					GL11.glTexCoord2f(((float)alphabet.getImageWidth()/(float)27)*(float)(((initials[2])-65)+1)/(float)alphabet.getTextureWidth(), 0);
					GL11.glVertex2f((float)100+ ((((float)8/(float)88)*(float)800)*(float)6) +(((float)4/(float)88)*(float)800), (float)150+(((float)1/(float)16)*(float)500) + (((float)4/(float)16)*(float)500));
				GL11.glEnd();
				
			}
		GL11.glPopMatrix();
	}

	
	public static void main(String[] argv) {
		DisplayExample displayExample = new DisplayExample();
		displayExample.start();
	}
}