package group3;
import group2.Blob;
import group2.TestBlobDetection;
import group2.TestPixel;
import group2.IBlobDetection;
import java.util.LinkedList;
import java.util.List;

public class MovingBlobDetectionTest implements IMovingBlobDetection {
	private List<MovingBlob> movingBlobs;
	
	public static void main(String[] args){
		TestBlobDetection test = new TestBlobDetection();
		MovingBlobDetectionTest movingtest = new MovingBlobDetectionTest();
		List<MovingBlob> list = movingtest.getMovingBlobs(test.getBlobs(null));	
	}
	
	public MovingBlobDetectionTest() {
		movingBlobs = new LinkedList<>();
	}
	
	// test data
	public List<MovingBlob> getMovingBlobs(List<Blob> blobList){
		for (Blob blob:blobList){
			this.movingBlobs.add(new MovingBlob(blob));
		}
		return movingBlobs;
	}
	
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