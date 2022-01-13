package engine;

import java.util.ArrayList;

public class vertice{
    ArrayList <aresta> listaAdjacentes = new ArrayList<aresta>();
    private int posLinha;
    private int posColuna;
    private vertice pai = null;
    private int f = 10000;
    private int g = 10000;
    private int h = 0;

    /**
     * Construtor que realiza o set da posição da linha e da coluna do vértice.
     * @param posL linha do vértice.
     * @param posC coluna do vértice.
     */
    public vertice(int posL, int posC){
        posLinha = posL;
        posColuna = posC;
    }
    
    /**
     * Reseta os valores de um vertice para o padrão
     */
    public void resetVertice(){
        f = 10000;
        g = 10000;
        h = 0;
        pai = null;
    }

    /**
     * Retorna o valor da linha do vértice.
     * @return linha do vértice.
     */
    public int getLinha(){
        return posLinha;
    }
    
    /**
     * Retorna o valor da coluna do vértice.
     * @return coluna do vértice.
     */
    public int getColuna(){
        return posColuna;
    }
    
    /**
     * Realiza o set da posição da linha em posLinha.
     * @param pLinha valor da linha.
     */
    public void setLinha(int pLinha){
        posLinha = pLinha;
    }
    
    /**
     * Realiza o set da posição da coluna em posColuna.
     * @param pColuna valor da coluna.
     */
    public void setColuna(int pColuna){
        posColuna = pColuna;
    }
    
    /**
     * Retorna o valor de F (somatória dos valores G e H) do vértice.
     * @return f do vértice.
     */
    public int getF(){
        return f;
    }
        
    /**
     * Retorna o valor de G (distância do fantasma até a posição atual) do vértice.
     * @return G do vértice.
     */
    public int getG(){
        return g;
    }
       
    /**
     * Retorna o valor de H (distância da posição atual até o pacman) do vértice.
     * @return H do vértice.
     */
    public int getH(){
        return h;
    }
    
    /**
     * Retorna o valor do pai do vértice atual.
     * @return vértice pai do vértice atual.
     */
    public vertice getPai(){
        return pai;
    }
    
    /**
     * Realiza o set do valor no valor de F.
     * @param pf valor de F.
     */
    public void setF(int pf){
        f = pf;
    }
        
    /**
     * Realiza o set do valor no valor de G.
     * @param pg valor de G.
     */
    public void setG(int pg){
        g = pg;
    }
    
    /**
     * Realiza o set do valor no valor de H.
     * @param ph valor de H.
     */
    public void setH(int ph){
        h = ph;
    }
  
    /**
     * Realiza o set do valor no vértice pai.
     * @param pPai vértice do pai.
     */
    public void setPai(vertice pPai){
        pai = pPai;
    }

    /**
     * Adiciona a aresta (ligação entre dois vértices) na lista de adjacentes.
     * @param a aresta do grafo.
     */
    public void adicionarAdjacente(aresta a){
        listaAdjacentes.add(a);
    }   
    
    /**
     * Retorna a lista de adjacentes para ser utilizada em outras classes.
     * @return lista de adjacentes.
     */
    public ArrayList <aresta> retornaLista(){
        return listaAdjacentes;
    }
}
