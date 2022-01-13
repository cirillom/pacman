package engine;

public class aresta{
    public vertice inicio;
    public vertice fim;

    /**
     * Construtor que realiza o set das psições.
     * @param start posição inicio do vertice.
     * @param end posição final do vertice.
     */
    public aresta(vertice start, vertice end) {
        inicio = start;
        fim = end;
    }
    
    /**
     * Retorna o valor da posição inicial da aresta.
     * @return valor da posição inicial da aresta.
     */
    public vertice getInicio(){
        return inicio;
    }
    
    /**
     * Retorna o valor da posição final da aresta.
     * @return valor da posição final da aresta.
     */
    public vertice getFim(){
        return fim;
    } 
}
