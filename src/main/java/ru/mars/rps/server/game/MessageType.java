package ru.mars.rps.server.game;

/**
 * Date: 01.11.14
 * Time: 17:40
 */
public class MessageType {
    public static final int C_PING = 0;//test
    public static final int S_PING = 1;//test
    public static final int C_PLAYER_WAITING = 2;//игрок сообщает, что хочет играть
    public static final int S_WAIT = 3;//сервер сообщает, что клиенту ищется пара
    public static final int C_PLAYER_CANCEL_WAIT = 4;//клиент расхотел играть
    public static final int S_PAIR_FOUND = 5;//пара найдена, клиент начинает прогрузку на экран игры
    public static final int C_READY_TO_PLAY = 6;//клиент прогрузил игру и запускаем таймер игроки ставят корабли
    public static final int S_TIME_OUT = 7;//сервер сообщает игроку, что время вышло
    public static final int C_PLAYER_FIELD = 8;//клиент передаёт на сервер своё поле, два массива 10х10
    public static final int S_GAME_READY = 9;//сервер сообщает о начале игры и передаёт номер ходящего игрока, запускаем таймер для ожидания хода игрока
    public static final int C_OK = 10;//клиент сообщает, что получил
    public static final int S_TIME_OUT_GAME = 11;//время для хода вышло, сообщаем о передаче хода противнику
    public static final int C_TARGET = 12;//игрок сообщает цель для выстрела
    public static final int S_TARGET = 13;//сервер передаёт противнику цель выстрела
    public static final int S_GAME_OVER = 15;//движение линии для клиента
}
