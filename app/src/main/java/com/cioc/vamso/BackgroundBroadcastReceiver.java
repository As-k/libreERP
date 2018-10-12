package com.cioc.vamso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

/**
 * Created by admin on 30/04/18.
 */

public class BackgroundBroadcastReceiver extends BroadcastReceiver {
    boolean res;
    SessionManager sessionManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        sessionManager = new SessionManager(context);
        res = sessionManager.getStatus();
        if (res) {
//            Toast.makeText(context, "loc broadcast service", Toast.LENGTH_SHORT).show();
            context.startService(new Intent(context, LocationService.class));
        } else {
            context.stopService(new Intent(context, LocationService.class));
        }

        if (sessionManager.getSessionId() != "" && sessionManager.getSessionId() != "") {
//            Toast.makeText(context, "loc B Destroy", Toast.LENGTH_SHORT).show();
            Session session = new Session();
            // Add all onJoin listeners
            session.addOnJoinListener(this::demonstrateSubscribe);

            // finally, provide everything to a Client and connect
            Client client = new Client(session, "ws://192.168.1.113:8080/ws", "default");
            CompletableFuture<ExitInfo> exitInfoCompletableFuture = client.connect();

//            context.startService(new Intent(context, BackgroundService.class));
        }


    }

    public void demonstrateSubscribe(Session session, SessionDetails details) {
        // Subscribe to topic to receive its events.
        CompletableFuture<Subscription> subFuture = session.subscribe("service.chat.admin",
                this::onEvent);
        subFuture.whenComplete((subscription, throwable) -> {
            if (throwable == null) {
                // We have successfully subscribed.
                System.out.println("Subscribed to topic " + subscription.topic);
            } else {
                // Something went bad.
                throwable.printStackTrace();
            }
        });
    }

    private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
        System.out.println(String.format("Got event: %s", args.get(0)));
    }
}