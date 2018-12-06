package com.gen.common.util;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PicUtil {
    public static String merge(Set<String> imgsSet, String targetFilePath)throws Exception{
        int baseN=52*2;
        int baseN_34=34*2;
        int mainN=213;
        BufferedImage image=new BufferedImage(mainN,mainN,BufferedImage.TYPE_3BYTE_BGR);

        int n=0;

        Graphics2D g=image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setBackground(new Color(247,247,247));

        g.clearRect(0, 0, mainN, mainN);
       // g.fillRect(0,0,110,110);
        g.setPaint(new Color(247,247,247));


        if(imgsSet!=null){
            int size=imgsSet.size();
           List<String> fileStr= new ArrayList<>(imgsSet);
           File inputFile=null;
            for(int i=0;i<size;i++){
                inputFile= new File(fileStr.get(i));
                if(!inputFile.exists()){
                    return targetFilePath;
                }
                BufferedImage img=ImageIO.read(inputFile);

                if(size==2){
                    g.drawImage(img, i*baseN+(i+1)*2, (mainN-baseN)/2, baseN, baseN, null);
                    img.flush();
                }else if(size==3){
                    if(i==0){
                        g.drawImage(img, (mainN-baseN)/2, 2, baseN, baseN, null);

                    }else{
                        g.drawImage(img, (i-1)*baseN+(i)*2, baseN+4, baseN, baseN, null);

                    }
                    img.flush();

                }else if(size==4 || size==5){
                    n=(i+1)%2==1?0:1;
                    if(i<=1){
                        g.drawImage(img, n*baseN+(n+1)*2, 2, baseN, baseN, null);
                        img.flush();
                    }else if(i<4){
                        g.drawImage(img, n*baseN+(n+1)*2, baseN+4, baseN, baseN, null);
                        img.flush();
                    }else{
                        BufferedImage centerImage = new BufferedImage(baseN, baseN, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2=centerImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setClip(new RoundRectangle2D.Double(0, 0, baseN, baseN, baseN*2/6, baseN*2/6));
                        g2.drawImage(img,0,0,baseN,baseN,null);
                        img.flush();
                        g2.dispose();
                        g.drawImage(centerImage, (mainN-baseN)/2, (mainN-baseN)/2, baseN, baseN, null);
                        centerImage.flush();
                    }

                }else if(size==6){
                    if(n>2){
                        n=0;
                    }
                    if(i<=2){
                        g.drawImage(img, n*baseN_34+(n+1)*2, mainN/3-baseN_34/2, baseN_34, baseN_34, null);

                    }else{

                        g.drawImage(img, n*baseN_34+(n+1)*2, mainN/3-baseN_34/2+baseN_34+2, baseN_34, baseN_34, null);

                    }
                    img.flush();
                    n++;
                }else if(size>6 && i<9){
                    int y=0;
                    if(i<=2){
                        y=2;
                    }else if(i>2 && i<=5){
                        y=baseN_34+4;
                    }else if(i>5 && i<=8){
                        y=baseN_34*2+6;
                    }
                    if(n>2){
                        n=0;
                    }
                    g.drawImage(img, n*baseN_34+(n+1)*2, y, baseN_34, baseN_34, null);
                    img.flush();
                    n++;
                }

            }
        }
        g.dispose();
        File desc=new File(targetFilePath);
        if(!desc.getParentFile().exists()){
            desc.getParentFile().mkdir();
        }
        FileOutputStream outPutFile=new FileOutputStream(desc);
        Thumbnails.of(image).scale(1).outputQuality(0.8f).outputFormat("jpeg").toOutputStream(outPutFile);
        IOUtils.closeQuietly(outPutFile);
        return targetFilePath;
    }

    public static void main(String[] args)throws Exception {
        //"C:\\Users\\Administrator\\Desktop\\ad7c096f7c3735784bb2809d82d24a09.jpg"
        List l=new ArrayList();
        l.add("D:\\data\\rs\\1513962411683.jpg");
        l.add("D:\\data\\rs\\1514029004171.jpg");
       // l.add("D:\\data\\rs\\20181014154530938.jpg");
      //  l.add("D:\\data\\rs\\20181114004128754.jpg");
       // l.add("D:\\data\\rs\\20181104201309066.jpg");
       // l.add("D:\\data\\rs\\20180415013050610-ico.jpg");



        PicUtil.merge(new HashSet<>(l),"D:\\test.jpg");
        System.out.println(3%3);
    }
}
