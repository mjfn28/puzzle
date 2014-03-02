package gltest;

public class Board {

	int[] b;
	int current=15;
	float red=1;
	float green=1;
	float blue=1;
	int count;
	
	public Board() {
		b= new int[16];
		
		for (int i=0; i<16; i++){
			b[i]=i;
		}	
		count=0;
	}

	public int getBoardPos(int x, int y){
		if (x<800){
			return ((x-1)/200) + 4*(3-((y-1)/200));
		} else {
			return -1;
		}
	}
	
	public void swap(int p1, int c){
		int tmp;
		if (p1!=-1){
			if (((p1==c-1) && ((p1/4)==(c/4))) || ((p1==c+1) && ((p1/4)==(c/4))) || (p1==c-4) || (p1==c+4))  {
				tmp=b[p1];
				b[p1]=b[c];
				b[c]=tmp;
				current=p1;
				count++;
			}
		}
	}
	
	public void shuffle(int steps){
		int Min=0;
		int Max=3;
		int move;
		int tmp;
		
		for(int i=0;i<steps;i++){
			move=Min+(int)(Math.random()*((Max-Min)+1));
			System.out.println(move);
			switch(move){
			case 0:
				tmp=current - 4;
				if((tmp>15) || (tmp<0)){
					i--;
				} else {
					swap(tmp, current);
				}
				break;
			case 1:
				tmp=current + 1;
				if((tmp>15) || (tmp<0)){
					i--;
				} else {
					swap(tmp, current);
				}
				break;
			case 2:
				tmp=current + 4;
				if((tmp>15) || (tmp<0)){
					i--;
				} else {
					swap(tmp, current);
				}
				break;
			case 3:
				tmp=current - 1;
				if((tmp>15) || (tmp<0)){
					i--;
				} else {
					swap(tmp, current);
				}
				break;
			default:
			  
			}
		}
		count=0;
	}
	
	public boolean checkWin(){
		boolean tmp=true;
		
		for (int i=0;i<16;i++){
			if ((b[i]==i) && tmp){
				tmp=true;
			} else {
				tmp=false;
			}
		}
		
		if (tmp) {
			red=0;
			green=1;
			blue=0;
		} else {
			red=1;
			green=1;
			blue=1;
		}
		
		return tmp;
	}
}
