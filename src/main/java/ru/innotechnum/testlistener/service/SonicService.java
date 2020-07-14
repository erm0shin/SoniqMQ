package ru.innotechnum.testlistener.service;

import ru.innotechnum.testlistener.integration.sonic.dto.XmlMessage;

public interface SonicService {

    void sendResponse(XmlMessage xmlMessage);

}
