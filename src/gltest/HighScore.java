package gltest;

import java.io.Serializable;

public class HighScore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2262061657729082206L;
	
	private int[] scores;
	private char[][] names;
	
	public HighScore() {
		
		scores=new int[10];
		names=new char[10][3];
		
		for (int i=0;i<10;i++){
			scores[i]=999;
			names[i][0]='N';
			names[i][1]='N';
			names[i][2]='E';
		}
	}
	
	public void addScore(int s, char[] n){
		int[] tmps= new int[10];
		char[][] tmpn= new char[10][3];
		
		boolean inserted = false;
		
		for (int i=0;i<10;i++){
			if (scores[i]>s){
				if (!inserted){
					tmps[i]=s;
					tmpn[i][0]=n[0];
					tmpn[i][1]=n[1];
					tmpn[i][2]=n[2];
					inserted=true;
				} else {
					tmps[i]=scores[i-1];
					tmpn[i][0]=names[i-1][0];
					tmpn[i][1]=names[i-1][1];
					tmpn[i][2]=names[i-1][2];
				}
			} else {
				tmps[i]=scores[i];
				tmpn[i][0]=names[i][0];
				tmpn[i][1]=names[i][1];
				tmpn[i][2]=names[i][2];
			}
		}
		
		for (int j=0;j<10;j++){
			scores[j]=tmps[j];
			names[j][0]=tmpn[j][0];
			names[j][1]=tmpn[j][1];
			names[j][2]=tmpn[j][2];
		}
	}

	public int getScore(int i){
		return scores[i];
	}
	
	public char[] getName(int i){
		return names[i];
	}
	
	public void printHS(){
		for (int i=0;i<10;i++){
			System.out.println("Name:"+names[i][0]+names[i][1]+names[i][2]+" Score:"+scores[i]);
		}
	}
}
