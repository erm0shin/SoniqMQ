package ru.innotechnum.testlistener.integration.sonic.listener;

import ru.innotechnum.testlistener.integration.sonic.dto.XmlMessage;

public interface SonicListener {

    void onMessage(XmlMessage xmlMessage);

}
