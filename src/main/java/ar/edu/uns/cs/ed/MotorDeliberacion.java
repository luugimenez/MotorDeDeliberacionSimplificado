package ar.edu.uns.cs.ed;

import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyStackException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;
import ar.edu.uns.cs.ed.tdas.tdamapeo.Map;
import ar.edu.uns.cs.ed.tdas.tdamapeo.Mapeo;
import ar.edu.uns.cs.ed.tdas.tdapila.PilaConArreglo;
import ar.edu.uns.cs.ed.tdas.tdapila.Stack;
import ar.edu.uns.cs.ed.tdas.tdaarbol.Arbol;
/**
 * Incluir los import necesarios para las clases utilizadas en el código.
 */
import ar.edu.uns.cs.ed.tdas.tdaarbol.Tree;
/**
 * Clase que representa el motor de deliveración.
 * 
 */
public class MotorDeliberacion {

    /**
     * Incluir los atributos necesarios para el desarrollo.
     */
    protected Tree<Opinion> arbol;
    protected Stack<Operaciones> pilaOperaciones;
    protected Map<String, Position<Opinion>> opinionesPorId;
    protected Map<String, PositionList<Opinion>> opinionesPorUsuario;

    /**
     * Constructor de la clase MotorDeliveracion.
     * Inicializa el árbol de debate, y las estructuras correspondientes.  
     * 
     */
    public MotorDeliberacion() {
        //TODO: Inicializar el árbol de debate y las estructuras correspondientes.
        arbol= new Arbol<Opinion>();
        pilaOperaciones = new PilaConArreglo<Operaciones>();
        opinionesPorId = new Mapeo<String,Position<Opinion>>();
        opinionesPorUsuario = new Mapeo<String,PositionList<Opinion>>();
    }

    /**
     * Registra una operación en la estructura que almacena las operaciones.
     * Las siguientes operaciones se pueden deshacer: "CREAR_DEBATE", "POSTEAR". El resto de las operaciones no se pueden deshacer.
     * 
     * La operación eliminar posteo no se puede deshacer, y si se intenta deshacer una operación cuando no hay operaciones registradas, se lanza una excepción.
     * 
     * 
     * @param operacion : La operación a registrar. 1: CREAR_DEBATE, 2: POSTEAR
     * @param detalle : Detalle de la operación, como el id del argumento o el id del usuario. El texto de la operación se almacena en mayúsculas para mantener consistencia. Por ejemplo: "CREAR_DEBATE:" + idRaiz.trim()
     * @param idArgumento : Identificador del argumento o de root en caso de ser el tema raíz.
     * @throws IllegalArgumentException si la operación es nula o vacía.
     */
    public void registrarOperacion(int operacion, String detalle, String idArgumento) {
        //TODO: Implementar el registro de la operación en la estructura correspondiente.
        if(operacion != 1 && operacion != 2) {
            throw new IllegalArgumentException("Operación inválida. Solo se permiten CREAR_DEBATE (1) y POSTEAR (2).");
        }
        if(detalle == null || detalle.trim().isEmpty()) {
            throw new IllegalArgumentException("Detalle de la operación inválido.");
        }
        if(idArgumento == null || idArgumento.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de argumento inválido.");
        }

        pilaOperaciones.push(new Operaciones(operacion, detalle.trim(), idArgumento.trim()));
    }

/**
 * Deshace la última operación registrada en el motor de deliveración.
 * La operación eliminar posteo no se puede deshacer, y si se intenta deshacer una operación cuando no hay operaciones registradas, se lanza una excepción.
 * @return
 * @throws EmptyStackException
 */
    public String deshacerUltimaOperacion() throws EmptyStackException {
        //TODO: Implementar la funcionalidad de deshacer la última operación registrada.
        Operaciones ultima = pilaOperaciones.top();
        String detalle = ultima.getDetalle();
        if(ultima.getOperacion() == 1){
            pilaOperaciones.pop();
            clearDebate();
            return detalle;
        }
        if(ultima.getOperacion() == 2){
            
            //tengo que pasarle los valores por parametro
            eliminarPosteo(null, null);
            pilaOperaciones.pop();
        }
        return null;
    }
/**
 * Crea un nuevo debate con un argumento raíz.
 * @param idRaiz   : Identificador único del tema raíz.
 * @param texto:    Texto del tema raíz.
 * @param idUsuario: Identificador del usuario.
 * @param nombreUsuario: Nombre del usuario.
 * 
 * El método valida que los parámetros no sean nulos o vacíos, y que el identificador de la raíz sea numérico.
 * Si el árbol ya tiene un debate, lanza una excepción.
 * El método registra la operación en la pila de operaciones para permitir deshacerla posteriormente.
 * @throws IllegalArgumentException si algún parámetro es inválido.
 * @throws IllegalStateException si ya existe un debate en curso.
 * Crea un usuario y un tema raíz, y los agrega al árbol y a las estructuras correspondientes.
 */
    public void crearDebate(String idRaiz, String texto, String idUsuario, String nombreUsuario) {
    //TODO: Implementar la creación del debate con el argumento raíz y el usuario correspondiente.
        if(idRaiz==null||idRaiz.trim().isEmpty()||texto==null||texto.trim().isEmpty()||idUsuario==null||idUsuario.trim().isEmpty()||nombreUsuario==null||nombreUsuario.trim().isEmpty())
            throw new IllegalArgumentException("datos ingresados invalidos");
        if(!arbol.isEmpty())
            throw new IllegalStateException("Ya existe un debate en curso");
        if (!idRaiz.trim().matches("[0-9]+")) 
            throw new IllegalArgumentException("El ID de la raíz debe ser numérico");        
        Usuario usuario1= new Usuario(idUsuario.trim(),nombreUsuario.trim());
        Opinion raiz= new Opinion(idRaiz.trim(),texto,"ROOT",usuario1);
        arbol.createRoot(raiz);
        opinionesPorId.put(idRaiz.trim(),arbol.root());
        PositionList<Opinion> lista= new ListaDoblementeEnlazada<Opinion>();
        lista.addLast(raiz);
        opinionesPorUsuario.put(idUsuario.trim(),lista);
        registrarOperacion(1, "CREAR_DEBATE:" + idRaiz.trim(), idRaiz.trim());
    }

    public void Postear(Usuario usr, String idNuevo, String idPadre, String postura, String texto) {
        //TODO: Implementar la funcionalidad de postear un nuevo argumento en el debate.
        if (arbol.isEmpty()) {
            throw new IllegalStateException("No hay debate en curso. Cree un debate primero.");
        }
        if (usr == null || idNuevo == null || idNuevo.trim().isEmpty() || idPadre == null || idPadre.trim().isEmpty()
            || postura == null || postura.trim().isEmpty() || texto == null || texto.trim().isEmpty())
        {
            throw new IllegalArgumentException("Los datos ingresados son inválidos.");
        }
        String nuevo = idNuevo.trim();
        String padreId = idPadre.trim();
        String post = postura.trim().toUpperCase(java.util.Locale.ROOT);
        if(!post.equals("AFAVOR") && post.equals("ENCONTRA")) {
            throw new IllegalArgumentException("La postura debe ser 'AFAVOR' o 'ENCONTRA'.");
        }
        if(opinionesPorId.get(nuevo) != null) {
            throw new IllegalArgumentException("Ya existe un argumento con el ID: " + nuevo);
        }
        Position<Opinion> Padre = opinionesPorId.get(padreId);
        if (Padre == null) {
            throw new IllegalArgumentException("No existe un argumento con el ID del padre: " + padreId);
        }
        
        Opinion opinion = new Opinion(nuevo, texto, post, usr);
        Position<Opinion> posicion = arbol.addLastChild(Padre, opinion);

        opinionesPorId.put(nuevo, posicion);

        PositionList<Opinion> delUsuario = opinionesPorUsuario.get(usr.getId());
        if (delUsuario == null) {
            delUsuario = new ListaDoblementeEnlazada<Opinion>();
            opinionesPorUsuario.put(usr.getId(), delUsuario);
        }
        delUsuario.addLast(opinion);

        registrarOperacion(2, "POSTEAR:" + nuevo, nuevo);
    }
    

    // Elimina un posteo y todo el subárbol.
    public void eliminarPosteo(String idUsuario, String idPosteo) {
    //TODO: Implementar la funcionalidad de eliminar un posteo y todo su subárbol en el debate.
    }

    //obtiene las posiciones en postorden.
    private void obtenerIdsYPosicionesSubarbol (Position<Opinion> nodo, PositionList<String> ids, PositionList<Position<Opinion>> posiciones) {
    //TODO: Implementar la funcionalidad de obtener los ids y posiciones de un subárbol en postorden.
    }

    //limpia todo el debate, dejando la estructura vacía.   
    public void clearDebate() {
    //TODO: Implementar la funcionalidad de limpiar todo el debate, dejando la estructura vacía.
        arbol = new Arbol<Opinion>();
        opinionesPorId = new Mapeo<String, Position<Opinion>>();
        opinionesPorUsuario = new Mapeo<String, PositionList<Opinion>>();

        while(!pilaOperaciones.isEmpty()) {
            pilaOperaciones.pop();
        }
    /*
    //este codigo es solo para un arbol con nodo raiz, tengo que modificar un caso general
        String idArgumento= (arbol.root()).element().getIdArgumento();
        opinionesPorId.remove(idArgumento);
        String idUsuario= (arbol.root()).element().getUsuario().getId();
        opinionesPorUsuario.remove(idUsuario);
        arbol.removeNode(arbol.root());
    */
    }

    //consultar por usuario, devuelve una lista de argumentos que pertenecen a ese usuario.
    public PositionList<Opinion> consultarArgumentosPorUsuario(String idUsuario) {
    //TODO: Implementar la funcionalidad de consultar los argumentos de un usuario específico en el debate.
        if(idUsuario == null || idUsuario.trim().isEmpty())
            throw new IllegalArgumentException("Id de usuario invalido");
        if(opinionesPorUsuario.get(idUsuario.trim()) == null)
            return new ListaDoblementeEnlazada<Opinion>();
        return opinionesPorUsuario.get(idUsuario.trim());
    }

 /*** Mostrar árbol por consola ***/
 
    /**
     * Imprime por consola, en modo texto, el contenido del árbol de debate,
     * con indentación jerárquica que muestra la estructura padre-hijo.
     * Cada nodo se muestra con su id, postura, usuario y texto del argumento.
     */
    public void imprimirDebate() {
        if (arbol == null || arbol.isEmpty()) {
            System.out.println("(El árbol de debate está vacío.)");
            return;
        }

        try {
            Position<Opinion> raiz = arbol.root();
            System.out.println(formatearNodo(raiz.element()));
            imprimirHijos(raiz, "");
        } catch (Exception e) {
            System.out.println("No se pudo imprimir el árbol: " + e.getMessage());
        }
    }

    /**
     * Imprime recursivamente los hijos de 'nodo', usando 'prefijo' para
     * mantener la indentación acumulada de los niveles superiores y
     * conectores tipo "├──" / "└──" para el nivel actual.
     */
    private void imprimirHijos(Position<Opinion> nodo, String prefijo) throws InvalidPositionException {
        PositionList<Position<Opinion>> hijos = new ListaDoblementeEnlazada<Position<Opinion>>();
        for (Position<Opinion> hijo : arbol.children(nodo)) {
            hijos.addLast(hijo);
        }


        for (Position<Opinion> hijo : arbol.children(nodo)) {
            boolean esUltimo = (hijo == hijos.last().element());
            
            String conector = esUltimo ? "└── " : "├── ";
            System.out.println(prefijo + conector + formatearNodo(hijo.element()));

            String prefijoHijo = prefijo + (esUltimo ? "    " : "│   ");
            imprimirHijos(hijo, prefijoHijo);
            
        }
    }

 /** Arma la línea de texto que representa a un Argumento en la impresión. */
    private String formatearNodo(Opinion a) {
        if (a == null) {
            return "(argumento nulo)";
        }
        return "[" + a.getIdArgumento() + "] (" + a.getPostura() + ") "
                + "usuario=" + a.getUsuario().getId() + " -> " + a.getTexto();
    }
 
    public int[] polarizacionDeNodo(Position<Opinion> polarizante){
        int [] record= new int[2];
        verificarOpinion(arbol.children(polarizante), record);
        return record;
    }
    
    private void verificarOpinion(Iterable<Position<Opinion>> hijos, int[] record){
        for(Position<Opinion> PosOp : hijos){
            Opinion op= PosOp.element();
            //visita
            if(op.getPostura().equals("A FAVOR")){
                record[0]+=1;
            } else if(op.getPostura().equals("EN CONTRA")){
                record[1]+=1;
            }
            verificarOpinion(arbol.children(PosOp), record);
        }
    }

    public PositionList<String[]> contabilizarTodaPolarizacion(){
        PositionList<String[]> ListaRecords = new ListaDoblementeEnlazada<>();
        if(arbol.root()!=null){
            contabilizarPolarizacion(arbol.root(), ListaRecords);
        }
        return ListaRecords;
    }

    private void contabilizarPolarizacion(Position<Opinion> nodo, PositionList<String[]> list){
        Opinion actual= nodo.element();
        String[] record= new String[3];
        record[0]= actual.getIdArgumento();
        int aFvor=0;
        int enContra=0;
        for(Position<Opinion> posOp: arbol.children(nodo)){
            contabilizarPolarizacion(posOp, list);
            Opinion op= posOp.element();
            if(op.getPostura().equals("A FAVOR")){
                aFvor++;
            }
            if(op.getPostura().equals("EN CONTRA")){
                enContra++;
            }
        }
        record[1] = String.valueOf(aFvor);
        
        list.addLast(record);
    }
}


