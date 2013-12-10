package de.dietzm.print;

import java.nio.ByteBuffer;

import de.dietzm.Constants;
import de.dietzm.gcodes.MemoryEfficientString;

/**
 * ReceiveBuffer 
 * A flexible buffer which provides functions to parse the byte[] content.
 * Avoid creating Strings to save memory
 * @author mdietz
 *
 */
public class ReceiveBuffer implements CharSequence {

	public byte[] array ;
	int offset, len;
	
	public ReceiveBuffer(int size){
		array = new byte[size];
		offset=0;
		len=0;
	}
	
	/**
	 * copy the content of the byte buffer up to the current position to offset 0
	 * @param buf
	 */
	public void put(ByteBuffer buf){
		if(buf.position() > array.length) throw new IndexOutOfBoundsException("ReceiveBuffer size exceeded");
		System.arraycopy(buf.array(), 0, array, 0, buf.position());
		len=buf.position();
	}
	
	/**
	 * copy the content of the byte buffer from offset 0 up to the current position to the end of this buffer
	 * @param buf
	 */
	public void append(ByteBuffer buf){
		//Log.d("SERIAL","AppendBuf len:"+len+" pos:"+buf.position());
		if(len+buf.position() > array.length) throw new IndexOutOfBoundsException("ReceiveBuffer size exceeded");
		System.arraycopy(buf.array(), 0, array, len, buf.position());
		len=len+buf.position();
	}
	
	/**
	 * clear buffer (set length to 0) 
	 */
	public void clear(){
		len=0;
	}
	
	public byte[] array() {
		return array;
	}

	public boolean isEmpty(){
		return len==0;
	}
	
	public boolean endsWithNewLine(){
		if(len==0) return false;
		return array[len-1]==Constants.newlineb;
	}
	
	public boolean startsWithOK(){
		if(len<2) return false;
		return array[0]==111 && array[1]==107; //ASCII
	}
	
	public boolean containsOK(){
		if(len<2) return false;
		for (int i = 0; i < len-1; i++) {
			if(array[i]==111 && array[i+1]==107) return true;
		}
		return false; //ASCII
	}
	
	public int indexOf(char ch){
		if(len<1) return -1;
		for (int i = 0; i < len-1; i++) {
			if(array[i]==ch) return i;
		}
		return -1; //ASCII
	}
	
	//Repetier firmware sends when send delay was too large ?! 
	public boolean containsWait(){
		if(len<4) return false;
		for (int i = 0; i < len-3; i++) {
			if(array[i]==119 && array[i+1]==97 && array[i+2]==105 && array[i+3]==116) return true;
		}
		return false; //ASCII
	}
	
	
	public boolean startsWithEcho(){
		if(len<4) return false;
		if(array[0]==101 && array[1]==99 && array[2]==104 && array[3]==111) return true;
		return false; //ASCII
	}
	
	
	/**
	 * Check if response is a plain "ok"
	 * @return
	 */
	public boolean isPlainOK(){
		//Log.d("SERIAL", "IS plainok:"+len+" "+startsWithOK()+endsWithNewLine());
		return len==3 && startsWithOK() && endsWithNewLine();
	}
	
	public String toString(){
		return new String(array,0,len);
	}
	

	/**
	 * Looks for T:
	 * @return
	 */
	public boolean containsTx(){
		if(len<2) return false;
		for (int i = 0; i < len-1; i++) {
			if(array[i]==84 && array[i+1]==58) return true;
		}
		return false; //ASCII
	}

	@Override
	public char charAt(int index) {
		return (char)array[index];
	}

	@Override
	public int length() {
		return len;
	}
	
	/**
	 * Call setlength if you manipulate the array directly
	 * @param newlen
	 */
	public void setlength(int newlen) {
		len=newlen;
	}


	@Override
	public CharSequence subSequence(int start, int end) {
		  if (start < 0 || end > (len)) {
			    throw new IllegalArgumentException("Illegal range " +
			      start + "-" + end + " for sequence of length " + length());
			  }
			  byte[] newdata = new byte[end-start];
			  System.arraycopy(array,start,newdata,0,end-start);
			  return new MemoryEfficientString(newdata);
	}
	
	public MemoryEfficientString subSequence(int start, int end, MemoryEfficientString str) {
		  if (start < 0 || end > (len)) {
			    throw new IllegalArgumentException("Illegal range " +
			      start + "-" + end + " for sequence of length " + length());
			  }
			  byte[] newdata = str.getBytes();
			  int len = Math.min(end-start, str.length());
			  System.arraycopy(array,start,newdata,0, len);
			  str.clear(len); 
			  return str;
	}
}