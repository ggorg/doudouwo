package com.ddw.util;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 二维码生成和读的工具类
 *
 */
public class QrCodeCreateUtil {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int margin = 0;
    private static final int LogoPart = 4;

    /**
     * 生成二维码矩阵信息
     * @param content 二维码图片内容
     * @param width 二维码图片宽度
     * @param height 二维码图片高度
     */
    public static BitMatrix setBitMatrix(String content, int width, int height){
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 指定编码方式,防止中文乱码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 指定纠错等级
        hints.put(EncodeHintType.MARGIN, margin); // 指定二维码四周白色区域大小
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitMatrix;
    }

    /**
     *
     * @param content
     * @param with
     * @param height
     * @param outStream
     * @param logoInput
     * @param title
     * @throws IOException
     */
    public static void createQrCode(String content,int with,int height, OutputStream outStream, InputStream logoInput,String title) throws IOException {

        BufferedImage image = toBufferedImage(setBitMatrix(content,with,height));
        // 加入LOGO水印效果
        if (logoInput!=null) {
            image = addLogo(image, logoInput);
        }
        if(StringUtils.isNotBlank(title)){
            image=pressText(title,image,with,height);
        }
        //Thumbnails.of(new FileInputStream("")).scale(1).outputQuality(0.5f).toOutputStream()
        Thumbnails.of(image).scale(1).outputQuality(0.3f).outputFormat("jpeg").toOutputStream(outStream);
        IOUtils.closeQuietly(outStream);
       // Thumbnails.of(image).asBufferedImage().

        //ImageIO.write(image, "jpeg", outStream);
    }

    /**
     * 生成二维码图片
     * @param matrix 二维码矩阵信息
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * 在二维码图片中添加logo图片
     * @param image 二维码图片
     * @param logoPath logo图片路径
     */
    public static BufferedImage addLogo(BufferedImage image, InputStream in) throws IOException {
        Graphics2D g = image.createGraphics();
        BufferedImage logoImage = ImageIO.read(in);
        // 计算logo图片大小,可适应长方形图片,根据较短边生成正方形
        int width = image.getWidth() < image.getHeight() ? image.getWidth() / LogoPart : image.getHeight() / LogoPart;
        int height = width;
        // 计算logo图片放置位置
        int x = (image.getWidth() - width) / 2;
        int y = (image.getHeight() - height) / 2;
        // 在二维码图片上绘制logo图片
        g.drawImage(logoImage, x, y, width, height, null);
        // 绘制logo边框,可选
        //g.drawRoundRect(x, y, logoImage.getWidth(), logoImage.getHeight(), 10, 10);
       // g.setStroke(new BasicStroke(2)); // 画笔粗细
        //g.setColor(Color.WHITE); // 边框颜色
       // g.drawRect(x, y, width, height); // 矩形边框
        logoImage.flush();
        g.dispose();
        in.close();
        return image;
    }
    public static BufferedImage pressText(String pressText,BufferedImage img, int width, int height) {
        int fonSize=12;
        // 计算文字开始的位置
        // x开始的位置：（图片宽度-字体大小*字的个数）/2
        int startX = (width-(fonSize*pressText.length()))/2;
        // y开始的位置：图片高度-（图片高度-图片宽度）/2
        int startY = height-(height-width)/2 + fonSize;
        System.out.println(startX+","+startY);
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(img, 0, 0, width, height, null);
           // Graphics g = img.createGraphics();
            g.setColor(Color.BLACK);
            g.setFont(new Font(null,1, fonSize));
            g.drawString(pressText, startX, startY);
            img.flush();
            g.dispose();

            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }
    /**
     * 为图片添加文字
     * @param pressText 文字
     * @param newImage 带文字的图片
     * @param targetImage 需要添加文字的图片
     * @param fontStyle 字体风格
     * @param color 字体颜色
     * @param fontSize 字体大小
     * @param width 图片宽度
     * @param height 图片高度
     */
    public static void pressText(String pressText, String newImage, String targetImage, int fontStyle, Color color, int fontSize, int width, int height) {
        // 计算文字开始的位置
        // x开始的位置：（图片宽度-字体大小*字的个数）/2
        int startX = (width-(fontSize*pressText.length()))/2;
        // y开始的位置：图片高度-（图片高度-图片宽度）/2
        int startY = height-(height-width)/2 + fontSize;
        try {
            File file = new File(targetImage);
            BufferedImage src = ImageIO.read(file);
            int imageW = src.getWidth(null);
            int imageH = src.getHeight(null);
            BufferedImage image = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(src, 0, 0, imageW, imageH, null);
            g.setColor(color);
            g.setFont(new Font(null, fontStyle, fontSize));
            g.drawString(pressText, startX, startY);
            g.dispose();
            FileOutputStream out = new FileOutputStream(newImage);
            ImageIO.write(image, "png", out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 测试代码
     * @throws WriterException
     */
    public static void main(String[] args) throws IOException, WriterException {
       // Thumbnails.of(file.getInputStream()).scale(1).outputQuality(0.5f).toFile(destFile);
       ;            Map map=new HashMap();
        map.put("tableNumber","005");
        map.put("storeId",1);
        map.put("storeName","门店公用事业公用事业公用事业");
        Map mainMap=new HashMap();
        mainMap.put("command","order");
        // mainMap.put("commandDesc","商城扫码下单");
        mainMap.put("data",map);
        createQrCode(JSONObject.toJSONString(mainMap),500,500,new FileOutputStream(new File("d:\\qrcode.jpg")), QrCodeCreateUtil.class.getClassLoader().getResourceAsStream("static/images/logo.png"),null);
       // createQrCode(new FileOutputStream(new File("d:\\qrcode.jpg")),"WE1231238239128sASDASDSADSDWEWWREWRERWSDFDFSDSDF123123123123213123",900,"JPEG");
       // readQrCode(new FileInputStream(new File("d:\\qrcode.jpg")));
    }

}
