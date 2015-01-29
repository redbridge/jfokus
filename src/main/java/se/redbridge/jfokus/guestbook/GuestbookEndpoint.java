/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

package se.redbridge.jfokus.guestbook;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/socket")
public class GuestbookEndpoint {
  private static final Logger LOGGER = Logger.getLogger(GuestbookEndpoint.class.getName());
  private static final String ATTR_MESSAGE_TYPE = "messageType";
  private static final String ATTR_MESSAGE = "message";
  private static final String ATTR_AUTHOR = "author";
  private static final String ATTR_CREATED = "created";
  private static final String ATTR_CONNECTIONS = "connections";
  private static final String MT_GUESTBOOK_ENTRY = "guestbookEntry";
  private static final String MT_NUM_CONNECTIONS = "numConnections";

  @Inject
  private GuestbookEAO eao;
  @Inject
  private GuestbookSessionHandler sessionHandler;

  @OnOpen
  private void onOpen(final Session session) {
    sessionHandler.addSession(session);
    sendCountToAll();
    sendMessages(session, 100);
  }

  @OnClose
  private void onClose(final Session session) {
    sessionHandler.removeSession(session);
    sendCountToAll();
  }

  @OnMessage
  private void onMessage(final String message) {
    try (final JsonReader reader = Json.createReader(new StringReader(message))) {
      final JsonObject json = reader.readObject();
      final GuestbookEntry guestbookEntry = eao.create(new GuestbookEntry(json.getString(ATTR_MESSAGE), json.getString(ATTR_AUTHOR)));
      sendMessageToAll(guestbookEntry);
    }
  }

  private void sendMessages(final Session session, final int max) {
    final List<GuestbookEntry> list = eao.fetchLatest(max);
    for (final GuestbookEntry guestbookEntry : list) {
      final String json = createEntryMessage(guestbookEntry);
      sendJsonToPeer(json, session);
    }
  }

  private void sendMessageToAll(final GuestbookEntry guestbookEntry) {
    final String json = createEntryMessage(guestbookEntry);
    final Iterator<Session> it = sessionHandler.getSessionIterator();
    while (it.hasNext()) {
      final Session session = it.next();
      sendJsonToPeer(json, session);
    }
  }

  private String createEntryMessage(final GuestbookEntry guestbookEntry) {
    return Json.createObjectBuilder().add(ATTR_MESSAGE_TYPE, MT_GUESTBOOK_ENTRY).add(ATTR_MESSAGE, guestbookEntry.getMessage()).add(
        ATTR_AUTHOR, guestbookEntry.getAuthor()).add(ATTR_CREATED, guestbookEntry.getCreated().getTime()).build().toString();
  }

  private String createConnectionsMessage() {
    return Json.createObjectBuilder().add(ATTR_MESSAGE_TYPE, MT_NUM_CONNECTIONS).add(ATTR_CONNECTIONS, sessionHandler.getCount()).build()
               .toString();
  }

  private void sendCountToAll() {
    final String json = createConnectionsMessage();
    final Iterator<Session> it = sessionHandler.getSessionIterator();
    while (it.hasNext()) {
      final Session session = it.next();
      sendJsonToPeer(json, session);
    }
  }

  private void sendJsonToPeer(final String json, final Session peer) {
    try {
      peer.getBasicRemote().sendText(json);
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, "Unable to send message to peer " + peer.getId(), e);
    }
  }
}
