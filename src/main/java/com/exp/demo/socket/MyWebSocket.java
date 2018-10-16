package com.exp.demo.socket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/websocket")
@Component
public class MyWebSocket {
	
	private static int onlineCount=0;
	
	private static CopyOnWriteArraySet<MyWebSocket> websocketset=new CopyOnWriteArraySet<>();
	
	//websocket session
	private Session session;
	
	@OnOpen
	public void onOpen(Session session) {
		this.session=session;
		websocketset.add(this);
		addOnlineCount();   //在线数+1
		System.out.println("在线数:"+getOnlineCount());
		try {
			sendMesssage("闪现呢");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnClose
	public void onClose() {
		websocketset.remove(this);
		subOnlineCount();
		System.out.println("关闭连接 :"+getOnlineCount());
	}
	
	@OnMessage
	public void onMessage(String message,Session session) {
		System.out.println("收到消息 ："+message);
		for(MyWebSocket socket:websocketset) {
			try {
				socket.sendMesssage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@OnError
	public void onError(Session session,Throwable error) {
		System.out.println("error++++++++++++++");
		error.printStackTrace();
	}

	public void sendMesssage(String msg) throws IOException {
		this.session.getBasicRemote().sendText(msg);
	}

	public static synchronized void addOnlineCount() {
		MyWebSocket.onlineCount++;
	}
	
	public static synchronized void subOnlineCount() {
		MyWebSocket.onlineCount--;
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}
}
