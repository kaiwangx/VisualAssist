package annotate;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CloudVisionClient {
  public static enum Status{
	  OK,ERROR;
  }
	
  public static Status annotate(Image img, StringBuilder result) {
	GoogleCredentials credentials = null;
	try {
		credentials = GoogleCredentials.fromStream(new FileInputStream("D:\\VizAssit\\VizAssist-4c63ca04adeb.json"))
			        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	ImageAnnotatorSettings imageAnnotatorSettings = null;
	try {
		imageAnnotatorSettings = ImageAnnotatorSettings.newBuilder()
				.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
				.build();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  
    // Instantiates a client
    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(imageAnnotatorSettings)) {

      // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
      AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
          .addFeatures(feat)
          .setImage(img)
          .build();
      requests.add(request);

      // Performs label detection on the image file
      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          System.out.printf("Error: %s\n", res.getError().getMessage());
          return Status.ERROR;
        }
        TextAnnotation annotation = res.getFullTextAnnotation();
        result.append(annotation.getText());
      }
    } catch (IOException e) {
		e.printStackTrace();
	}
    return Status.OK;
  }
}
