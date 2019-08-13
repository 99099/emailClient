package model;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.QuotedPrintable;


public class Mail {

	public String from;
	public String to;
	public String date;
	public String subject;
	public String content="";
	public Mail(String from, String to, String date, String subject, String content) {
		this.from = from;
		this.to = to;
		this.date = date;
		this.subject = subject;
		this.content = content;
	}
	
	public Mail(Vector<String> messages) {
		getMailFromMessages(messages);
	}
	public Mail() {
		
	}
	public String getSubject() {
		return this.subject;
	}
	public String getSender() {
		return from;
	}
	public String getContent() {
		return this.content;
	}
	public String toString() {
		return "Subject:" + subject + "\r\n" + content + "\r\n";
	}
	public void getMailFromMessages(Vector<String> messages) {
		subject = "";
		content = "";
		
		String contentType = "";//定义邮件内容类型
		String contentCharset = "";//定义邮件编码方式
		String contentEncrypt = "";//定义邮件加密方式
		to = AppData.user;
		int index = 0;
		
		int size = messages.size();
		boolean hasContent = false;
		boolean isContent = true;
		boolean start = false;
		//邮件主题符合该正则表达式
		String regex = "\\?(.*)\\?(B|Q|b|q)\\?(.*)\\?=$";

		while(index < size) {
			isContent = true;
			String temp = messages.get(index);
			
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(temp);
			if(m.find()) {
				subject += decodeSubject(m.group());
//				System.out.println("myProject: "+m.group());
//				System.out.println("我的主题:"+subject);
				index++;
				continue;
			}
			//得到发件人
			if(temp.startsWith("From")) {
				String regex1 = "<(.*)@(.*)>";
				Pattern p1 = Pattern.compile(regex1);
				Matcher m1 = p1.matcher(temp);
				if(m1.find()) {
					from = m1.group();
//					System.out.println(from);
					index++;
					continue;
				}
			}
			//得到邮件日期
			else if(temp.startsWith("Date")) {
				date = temp.substring(5);
//				System.out.println(date);
				index++;
				continue;
			}
			else if(temp.startsWith("Subject")) {
				subject = temp.substring(8);
				index++;
				continue;
			}
			//对邮件内容进行解码
			else if(temp.startsWith("Content-Type") && temp.endsWith(";")) {
				contentType = getContentType(temp);
				String tempNextLine = messages.get(index+1);
				int firstQuotationMark = tempNextLine.indexOf("=");
				contentCharset = tempNextLine.substring(firstQuotationMark+1);
				isContent = false;
				hasContent = true;
			}
			else if(temp.startsWith("Content-Type")) {
				contentType = getContentType(temp);
				int firstQuotationMark = temp.indexOf("=");
				contentCharset = temp.substring(firstQuotationMark+1);
				isContent = false;
				hasContent = true;
			}
			else if(temp.startsWith("Content-Transfer-Encoding")) {
				int colonIndex = temp.indexOf(":");
				contentEncrypt = temp.substring(colonIndex+2);
			}
			else if(temp.equals("")) {
				start = true;
			}
			else if(temp.startsWith("--")) {
				hasContent = false;
				start = false;
				index++;
				continue;
			}
			if(hasContent && isContent && start) {
				if(contentType.equals("text/html") || contentType.equals("text/plain")) {
					if(contentEncrypt.equals("base64")) {
						byte [] decodeMessageBytes = Base64.getDecoder().decode(temp);
						try {
							content += new String(decodeMessageBytes, contentCharset);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							content+=(temp+"\r\n");
						}
						
					}
					else if(contentEncrypt.equals("quoted-printable")) {
						content += (QuotedPrintable.decode(temp.getBytes(), contentCharset)+"\r\n");
					}
					else {
						content+=(temp+"\r\n");
					}
					index++;
					continue;
				}
				else {
					content+=(temp+"\r\n");
					index++;
					continue;
				}
			}
			index++;
		}
	}
	
	public String getContentType(String res) {
		
		String regex2 = ":\\s(.*)\\/(.*);";
		Pattern p2 = Pattern.compile(regex2);
		Matcher m2 = p2.matcher(res);
		if(m2.find()) {
			return m2.group().substring(2, m2.group().length()-1);
		}
		return "";
	}
	//提取出内容
	public String decodeContent(String res) {
		
		return null;
		
	}
	//解析得到主题
	public String decodeSubject(String res) {
		StringTokenizer st = new StringTokenizer(res, "?");
		String charset = st.nextToken();
		String encryptedMethod = st.nextToken();
		String message = st.nextToken();
		if(encryptedMethod.toUpperCase().equals("B")) {
			byte [] decodeMessageBytes = Base64.getDecoder().decode(message);
			try {
				return new String(decodeMessageBytes, charset);
//				System.out.println();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			return QuotedPrintable.decode((message).getBytes(), charset);
		}
		return "";	
	}
}
