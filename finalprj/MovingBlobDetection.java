/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomouscarfinalprogram;

import java.util.LinkedList;
import java.util.List;

public class MovingBlobDetection implements IMovingBlobDetection {
	private List<MovingBlob> movingBlobs;
	
	public MovingBlobDetection() {
		movingBlobs = new LinkedList<>();
	}
	
	public List<MovingBlob> getMovingBlobs(List<Blob> blobList){
		updateMovingBlobs(blobList);
		return movingBlobs;
	}
	
	// test data is in "MovingBlobDetectionTest.java"
	
	private void updateMovingBlobs(List<Blob> blobList){
		for(Blob blob:blobList){
			for(MovingBlob movingBlob:movingBlobs){
				if(blob.color.getColor()==movingBlob.color.getColor()){
					int distanceX = Math.abs(movingBlob.predictedX-blob.centerX);
					int distanceY = Math.abs(movingBlob.predictedY-blob.centerY);
					int distance = (int) Math.sqrt(distanceX*distanceX+distanceY*distanceY);
					
					//use distance
					
					
				}
			}
		}
		
	}

	
	private void calculateVelocity(MovingBlob movingBlob, Blob newBlob){
		int movementX = newBlob.centerX - movingBlob.centerX;
		int movementY = newBlob.centerY - movingBlob.centerY;
		
		movingBlob.velocityX += movementX;
		movingBlob.velocityX /= 2;
		movingBlob.velocityY += movementY;
		movingBlob.velocityY /= 2;
	}
}