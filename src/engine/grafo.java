package engine;
import java.util.ArrayList;

import elements.PacManModel;
import elements.PacManModel.CellValue;

public class grafo {
    ArrayList <vertice> listaVertices = new ArrayList<vertice>();
    ArrayList <aresta> listaArestas = new ArrayList<aresta>();      
    
    /**
     * Adiciona o vertice na lista de vértices.
     * @param linha linha do vertice.
     * @param coluna coluna do vertice.
     */
    public void adicionaVertice(int linha, int coluna) {
        vertice v = new vertice(linha, coluna);
        // Adiciona na lista de vértices
        listaVertices.add(v);
    }

    /**
     * Adiciona a aresta na lista de arestas e na lista de adjacências.
     * @param inicio vértice inicial da aresta.
     * @param fim vértice final da aresta.
     */
    public void adicionaAresta(vertice inicio, vertice fim) {
        aresta a = new aresta(inicio, fim);
        // Adiciona na lista de adjacencias
        inicio.adicionarAdjacente(a);
        // Adiciona na lista de arestas
        listaArestas.add(a);                
    }
    
    /**
     * Adiciona os vértices e as arestas se forem posições válidas do tabuleiro.
     * @param model The game model
     */
    public void AdicionaGrafo(PacManModel model){
        for (int i = 0; i < model.getRowCount(); i++){
            for (int j = 0; j < model.getColumnCount(); j++){
                // Verifica se a posicao é valida para a criação de um vertice
                if (model.getCellValue(i, j) != CellValue.WALL){
                    adicionaVertice(i,j);
                }
            }
        }
        
        for (int i = 0; i < this.listaVertices.size(); i++){      
            int linha = listaVertices.get(i).getLinha();
            int coluna = listaVertices.get(i).getColuna();
           
            // Faz as verificações das 4 direções de um vértice (cima, baixo, direita e esquerda) para inserir uma aresta
            procuraAdicaoAresta(listaVertices.get(i),linha-1, coluna);
            procuraAdicaoAresta(listaVertices.get(i),linha+1, coluna);
            procuraAdicaoAresta(listaVertices.get(i),linha, coluna+1);
            procuraAdicaoAresta(listaVertices.get(i),linha, coluna-1);
        }
    }     
    
    /**
     * Procura a adjacência na lista de vértices e insere a aresta se os parâmetros forem válidos.
     * @param i vertice inicial da aresta.
     * @param linhap linha da posição final do vértice.
     * @param colunap coluna da posição final do vértice.
     */
    public void procuraAdicaoAresta(vertice i, int linhap, int colunap){
        for (int j = 0; j < this.listaVertices.size(); j++){ 
            // Procura a linha e a coluna na lista de vértices
            if (linhap == listaVertices.get(j).getLinha() && colunap == listaVertices.get(j).getColuna()){
                // Se encontrado, adiciona uma aresta na lista de arestas
                adicionaAresta(i, listaVertices.get(j));
            }
        }
    }    
    
    /**
     * Procura o vértice na lista de vértices de acordo com os parâmetros.
     * @param linha linha do vértice procurado.
     * @param coluna coluna do vértice procurado.
     * @return vértice encontrado (nulo ou não).
     */
    public vertice procuraVertice(int linha, int coluna){
        for (int j = 0; j < this.listaVertices.size(); j++){ 
            // Procura a linha e a coluna na lista de vértices
            if (linha == listaVertices.get(j).getLinha() && coluna == listaVertices.get(j).getColuna()){
                // Retorna o vertice
                return listaVertices.get(j);
            }
        }
        return null;
    }   
}