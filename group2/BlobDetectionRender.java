package group2;

import java.util.List;

import group1.FileImage;
import group1.IImage;
import group1.IPixel;
import group1.Image;
import group1.Pixel;
import group3.IMovingBlobDetection;
import group3.MovingBlob;
import group3.MovingBlobDetection;
import group4.BlobFilter;
import group4.IMovingBlobReduction;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class BlobDetectionRender extends Application
{
    boolean drawBlobs = true;
    boolean filter = true;
    boolean posterize = true;
    
    final long calTime = 2_000_000_000L;
    long lastTime = -1;
    long cumulativeTime = calTime;
    
    int framesPerCall = 6;
    int currentFrame = 0;
    
    
    public static void main(String... args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        IBlobDetection blobDetect = new BlobDetection();
        IMovingBlobDetection movingBlobDetect = new MovingBlobDetection();
        IMovingBlobReduction blobFilter = new BlobFilter();
        
        // IImage image = new JpgImage("src/testImage1.png");
        IImage image = new FileImage();
        
        IPixel[][] pixels = image.getImage();
        final int scale = 2;

        if(pixels.length == 0)
        {
            System.err.println("Please plug in the camera.");
            System.exit(1);
        }
        
        final int width = pixels[0].length;
        final int height = pixels.length;

        Canvas canvas = new Canvas(width * scale, height * scale);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        AnimationTimer timer = new AnimationTimer() {
        	@Override
        	public void handle(long time)
        	{
        		/*
        		if(lastTime != -1)
        		{
        			cumulativeTime += (time - lastTime);
        		}
        		
        		lastTime = time;
        		
        		if(++currentFrame != framesPerCall && image instanceof FileImage)
        		{
        		    return;
        		}
        		
        		currentFrame = 0;
        		
        		if(cumulativeTime >= calTime)
        		{
        			cumulativeTime = 0;
        			image.autoColor();
        		}
        		*/
        		
		        image.readCam();
		        IPixel[][] pixels = image.getImage();
		
		        final int width = pixels[0].length;
		        final int height = pixels.length;
		
		        final float blockedOutArea = (0);
		        for (int i = 0; i < width; i++)
		        {
		            for (int j = 0; j < height; j++)
		            {
		                if (j < (height * blockedOutArea))
		                {
		                    gc.setFill(Color.RED);
		                    pixels[j][i] = new Pixel((short) 255, (short) 0, (short) 0);
		                }
		                else
		                {
		                    //@formatter:off
		                    IPixel p = pixels[j][i];
		                    Paint fill = Color.rgb(p.getRed(), p.getGreen(), p.getBlue());
		                    
		                    if(posterize)
		                    {
		                    	fill = getPaint(p);	
		                    }
		                    
		                    gc.setFill(fill);
		                    
		                    //@formatter:on
		                }
		
		                gc.fillRect(i * scale, j * scale, scale, scale);
		            }
		        }
		
		        List<Blob> blobs = blobDetect.getBlobs(image);
		        List<MovingBlob> movingBlobs =
		                movingBlobDetect.getMovingBlobs(blobs);
		        
		        List<MovingBlob> unifiedBlobs = movingBlobDetect.getUnifiedBlobs(movingBlobs);
		        /*
		        for(MovingBlob b : movingBlobs)
		        {
		            System.out.println(b);
		        }*/
		        List<MovingBlob> filteredBlobs = blobFilter.reduce(unifiedBlobs);
		        		       
		        gc.setStroke(Color.DARKGOLDENROD);
		        gc.setLineWidth(4);
		        
		        if(drawBlobs)
		        {
		        	if(filter)
		        	{
				        for (Blob blob : filteredBlobs)
				        {
				            gc.strokeRect(blob.x * scale, blob.y * scale, blob.width * scale, blob.height * scale);
				        }
		        	}
		        	else
		        	{
				        for (Blob blob : unifiedBlobs)
				        {
				            gc.strokeRect(blob.x * scale, blob.y * scale, blob.width * scale, blob.height * scale);
				        }
		        	}
		        }
	        }
        };
        
        
        timer.start();
        
        primaryStage.setTitle("JavaFX Window");

        Group rootNode = new Group();
        rootNode.getChildren().addAll(canvas);

        Scene myScene = new Scene(rootNode, width * scale, height * scale);
        primaryStage.setScene(myScene);

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				switch(arg0.getCode())
				{
				case P:
					posterize = !posterize;
					break;
				case B:
					drawBlobs = !drawBlobs;
					break;
				case F:
					filter = !filter;
					break;
				case ESCAPE:
				    image.finish();
				    System.exit(0);
				    break;
				    
				default:
					break;
				}
			}
        });
        primaryStage.show();
    }

    private static Paint getPaint(IPixel p)
    {
        switch (p.getColor())
        {
            case 0:
                return (Color.RED);
            case 1:
                return (Color.LIME);
            case 2:
                return (Color.BLUE);
            case 3:
                return (Color.GRAY);
            case 4:
                return (Color.BLACK);
            case 5:
                return (Color.WHITE);
            default:
                throw new IllegalStateException("Invalid color code " + p.getColor() + ".");
        }
    }
}
