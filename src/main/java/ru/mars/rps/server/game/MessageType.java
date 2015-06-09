package ru.mars.rps.server.game;

/**
 * Date: 01.11.14
 * Time: 17:40
 */
public class MessageType {
//    public static final int C_PING = 0;//test
//    public static final int S_PING = 1;//test
    //    public static final int C_LOGIN = 98;
//    public static final int S_LOGIN = 99;
    public static final int C_PLAYER_WAITING = 96;//игрок сообщает, что хочет играть
    public static final int S_WAIT = 97;//сервер сообщает, что клиенту ищется пара
    public static final int C_PLAYER_CANCEL_WAIT = 94;//клиент расхотел играть
    public static final int S_GAME_CANCELLED = 95;//клиент расхотел играть, возвращаем в лобби
    public static final int S_PAIR_FOUND = 93;//пара найдена, клиент начинает прогрузку на экран игры
    public static final int C_READY_TO_PLAY = 92;//клиент прогрузил игру
    public static final int S_GAME_READY = 91;//сервер сообщает о начале игры
    public static final int C_PLAYER_SELECTED = 90;//клиент выбрал элемент
    public static final int S_PLAYER_SELECTED = 89;//сервер сообщает что выбрал другой игрок и кто выиграл
    public static final int C_PLAYER_CANCELLED_GAME = 88;//клиент расхотел играть и сдаётся
    public static final int S_GAME_OVER = 87;//игра окончена
}
