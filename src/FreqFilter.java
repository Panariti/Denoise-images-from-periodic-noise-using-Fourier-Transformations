
import java.lang.Math.*;
import java.awt.*;

/**
 * Class containing method to apply a frequency filter to an image.
 *
 * @author Simon Horne.
 */
public class FreqFilter{

  /**
   * Method to apply a high or low pass filter to an image.
   *
   * @param input TwoDArray representing the image.
   * @param h boolean true - highpass, false - lowpass.
   * @return TwoDArray representing new image.
   */
public static TwoDArray filter(TwoDArray input, int r, int bandwidth){
    TwoDArray output = new TwoDArray(input.values,input.size,input.size);
				     
    int i2,j2;
    double r2 = r*r;
    int c = 2*r+500;
      for(int j=0;j<input.size;++j){
	for(int i=0;i<input.size;++i){
	  if(i>=input.size/2) i2=i-input.size;
	  else i2=i;
	  if(j>=input.size/2) j2=j-input.size;
	  else j2=j;
	  double d2 = i2*i2 + j2*j2;
	  if(d2>(r-bandwidth/2)*(r+bandwidth/2) && d2 < (r+bandwidth/2)*(r+bandwidth/2)) output.values[i][j] = new ComplexNumber(0,0);
	  else output.values[i][j] = input.values[i][j];
	}
      }
    
    return output;
  }

  
  public static TwoDArray filter2(TwoDArray input, int x, int y, int size, int region)
  {
      TwoDArray output = new TwoDArray(input.values,input.size,input.size);
      int x1 = x; int y1 = y;
      System.out.println(input.size);
      
      if(region == 1)
      {
          boolean error = false;
          for (int i = 0; i < size; i++) 
          {
              for (int j = 0; j < size; j++) 
              {
                  int k1 = i + input.size / 2 + x;
                  int k2 = j + input.size / 2 + y;
                  if((k1<input.size)&&(k1>0)&&(k2<input.size)&&(k2>0))
                  output.values[k1][k2] = new ComplexNumber(0, 0);
                  else
                      error = true;
              }
          }
          if(error)
              System.out.println("Provoni perseri!");
      }
      else if(region == 3)
      {
          y = y - input.size/2;
          boolean error = false;
          for (int i = 0; i < size; i++) 
          {
              for (int j = 0; j < size; j++) 
              {
                 int k1 = i + input.size / 2 + x;
                 int k2 = j + y;
                 if((k1<input.size)&&(k1>0)&&(k2<input.size)&&(k2>0))
                  output.values[i + input.size / 2 + x][j + y] = new ComplexNumber(0, 0);
              }
          }
          if(error)
              System.out.println("Provoni perseri!");
      }
      else if(region == 2)
      {
          x = x - input.size/2;
          boolean error = false;
          for (int i = 0; i < size; i++) 
          {
              for (int j = 0; j < size; j++) 
              {
                  int k1 = i+x;
                  int k2 = j+input.size/2+y;
                  if((k1<input.size)&&(k1>0)&&(k2<input.size)&&(k2>0))
                  output.values[i + x][j+input.size/2+y] = new ComplexNumber(0, 0);
              }
          }
          if(error)
              System.out.println("Provoni perseri!");
      }
      else if(region == 4)
      {
          boolean error = false;
          for (int i = 0; i < size; i++) 
          {
              for (int j = 0; j < size; j++) 
              {
                  int k1 = i + x - input.size/2;
                  int k2 = j + y - input.size/2;
                  if((k1<input.size)&&(k1>0)&&(k2<input.size)&&(k2>0))
                  output.values[i + x - input.size/2][j + y - input.size/2] = new ComplexNumber(0, 0);
              }
          }
          if(error)
              System.out.println("Provoni perseri!");
      }
          
      return output;
  }
  
  public static TwoDArray filter3(TwoDArray input, int x, int y)
  {
      TwoDArray output = new TwoDArray(input.values,input.size,input.size);
      int i2,j2;
//      double r2 = r*r;
      for(int j=0;j<input.size;++j)
      {
	for(int i=0;i<input.size;++i){
	  if(i>=input.size/2) i2=i-input.size;
	  else i2=i;
	  if(j>=input.size/2) j2=j-input.size;
	  else j2=j;
	  if((j2<x-50) && (j2>x+50) && (i2<y-50) && (i2>y+50))
          {
              output.values[i][j] = new ComplexNumber(0,0);
          }
          else
          {
              output.values[i][j] = input.values[i][j];
          }
	}
      }
      return output;
  }
}
