package com.ad.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ad.constant.FileType;
import com.ad.domain.XResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

@Controller
@RequestMapping("/file")
public class FileController{

	private static Log log = LogFactory.getLog(FileController.class);
	/**
	 * 文件上传处理
	 * */
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public XResponse upload(HttpServletRequest request, HttpServletResponse response){
		XResponse xResponse = new XResponse();
		
		try {
			ServletContext svlCtx = request.getSession().getServletContext();
			
			CommonsMultipartResolver multiResolver = new CommonsMultipartResolver(svlCtx);
			if(multiResolver.isMultipart(request)){
				
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				//先保存图片
				saveAdResource(multiRequest);
				//再保存二维码
				saveHref(multiRequest);
			}
		} catch (Exception e) {
			xResponse.setReturnCode(1);
			xResponse.setReturnMessage(e.getMessage());
		}
		
		return xResponse;
	}
	
	/**
	 * 保存资源链接
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws WriterException 
	 * */
	private void saveHref(MultipartHttpServletRequest multiRequest) throws FileNotFoundException, IOException, WriterException{
		log.debug("准备保存资源链接....");
		String svlCtxRealPathWithSeparator = multiRequest.getSession().getServletContext().getRealPath("/") + System.getProperty("file.separator");
		String imgPath = svlCtxRealPathWithSeparator + "ad-img" + System.getProperty("file.separator");
		// 图片的资源指向
		String adHrefPath = this.getClass().getClassLoader().getResource("ad-href.properties").getPath();
		Properties props = new Properties();
		props.load(new FileInputStream(adHrefPath));
		//页面传过来的资源指向
		Enumeration<String> enume = multiRequest.getParameterNames();
		while(enume.hasMoreElements()){
			String paramName = (String) enume.nextElement();
			String paramValue = StringUtils.trimWhitespace(multiRequest.getParameter(paramName));
			props.setProperty(paramName, StringUtils.isEmpty(paramValue) ? props.getProperty(paramName, "") : "http://" + paramValue);
			log.debug("已保存 " + paramName + ":" + paramValue);
			//新的资源指向则需要生成新的二维码
			if(!StringUtils.isEmpty(paramValue)){
				generateQRCode(imgPath, paramName, paramValue);
			}
		}
		OutputStream os = new FileOutputStream(adHrefPath);
		props.store(os, "by xin_jing_bao@yeah.net");
		os.close();
		log.debug("结束保存资源链接....");
	}
	
	/**
	 * 根据资源链接生成二维码并保存
	 * @throws WriterException 
	 * @throws IOException 
	 * */
	private static void generateQRCode(String imgPath, String imgName, String uri) throws WriterException, IOException{
		
		int width = 200, height=200;
		String type = "png";
		
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 0);
		
		BitMatrix bitMatrix = new MultiFormatWriter().encode(uri, BarcodeFormat.QR_CODE, width, height, hints);
		File file = new File(imgPath, imgName + "." + type);
		//MatrixToImageWriter.writeToFile(bitMatrix, type, file);
		
		FileOutputStream fos = new FileOutputStream(file);
		MatrixToImageWriter.writeToStream(bitMatrix, type, fos);
		fos.close();
		
		log.debug("二维码已生成: " + file);
	}
	
	/**
	 * 保存资源文件
	 * @throws Exception 
	 * */
	private void saveAdResource(MultipartHttpServletRequest multiRequest) throws Exception{
		Iterator<String> iterator = multiRequest.getFileNames();
		String svlCtxRealPathWithSeparator = multiRequest.getSession().getServletContext().getRealPath("/") + System.getProperty("file.separator");
		while(iterator.hasNext()){
			
			MultipartFile file = multiRequest.getFile(iterator.next());
			if(!file.isEmpty() && !StringUtils.isEmpty(file.getOriginalFilename())){
					
					String imgPath = svlCtxRealPathWithSeparator + "ad-img" + System.getProperty("file.separator");
					String imgSuffix = file.getContentType().substring(file.getContentType().indexOf("/") + 1);
					
					FileType imgType = FileType.TXT;
					for(FileType type : FileType.values()){
						
						if(file.getContentType().equals(type.desc())){
							
							imgType = type;
							break;
						}
					}
					switch(imgType){
					case PNG:
					case JPG:
						break;
					default:
						throw new Exception("文件类型仅限JPG、PNG, [" + file.getOriginalFilename() + "]类型错误.");
					}
					
					file.transferTo(new File(imgPath + file.getName() + "." + "png"));
					log.debug("资源 " + file.getName() + ".png" + " 已保存");
			}
		}
	}
	
	@RequestMapping("/index")
	public ModelAndView getView(){
		
		return new ModelAndView("index");
	}
/*
	@ResponseBody
	@ExceptionHandler(Exception.class)
	public XResponse error(Exception e, HttpServletRequest request, HttpServletResponse response){
		
		
		xResponse.setReturnCode(1);
		xResponse.setReturnMessage(e instanceof org.springframework.web.multipart.MaxUploadSizeExceededException ? 
				"超出文件大小限制, 一次上传的文件总大小不能超过 20M ." : e.getMessage());
		
		return xResponse;
	}
	*/
	/*
	@Override
	public ModelAndView resolveException(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3) {

		Map<String, Object> xResponse = new HashMap<>();
		xResponse.put("returnCode", 1);
		xResponse.put("returnMessage", "出错了, 请检查请求 OR 稍后再试.");
		
		return new ModelAndView("error", xResponse);
	}
	*/
}
