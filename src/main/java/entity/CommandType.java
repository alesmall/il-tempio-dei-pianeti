package entity;

/**
 * l'enum dei tipi di comandi disponibili nel gioco.
 */
public enum CommandType {
    /** mostra i comandi disponibili */
    AIUTO,

    /** muovi il personaggio verso nord */
    NORD,

    /** muovi il personaggio verso sud */
    SUD,

    /** muovi il personaggio verso est */
    EST,

    /** muovi il personaggio verso ovest */
    OVEST,

    /** mostra l'inventario del giocatore */
    INVENTARIO,

    /** prendi un oggetto */
    PRENDI,

    /** lascia un oggetto */
    LASCIA,

    /** osserva la stanza o un oggetto */
    OSSERVA,

    /** usa un oggetto o un oggetto su un altro oggetto */
    USA,

    /** unisci due oggetti */
    UNISCI
}
