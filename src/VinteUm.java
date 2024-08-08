import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.random.*;
import javax.swing.*;

public class VinteUm {
    private class Carta {
        String valor;
        String naipe;


        Carta(String valor, String naipe) {
            this.valor = valor;
            this.naipe = naipe;
        }

        public String toString() {
            return this.valor + "-" + this.naipe;
        }

        public int getValor() {
            if ("AJQK".contains(valor)) {
                if (valor == "A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(valor);
        }

        public boolean isAs() {
            return valor == "A";
        }

        public String getCaminhoImagem() {
            return "./cards/" + toString() + ".png";
        }
    }

    ArrayList<Carta> baralho;
    Random random = new Random();

    //Dealer
    Carta dealerCartaSecreta;
    ArrayList<Carta> dealerCartas;
    int dealerSoma;
    int dealerAsesTotal;
    
    //Jogador
    ArrayList<Carta> jogadorCartas;
    int jogadorSoma;
    int jogadorAsesTotal;

    //Janela do Jogo
    int janelaLargura = 600;
    int janelaAltura = janelaLargura;
    int cartaLargura = 110; //Razão é 1/1.4
    int cartaAltura = 154;

    JFrame janelaJogo = new JFrame("Vinte e Um");
    JPanel painelJogo = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                //Carta Secreta
                Image cartaSecretaImage = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!botaoParar.isEnabled()) {
                    cartaSecretaImage = new ImageIcon(getClass().getResource(dealerCartaSecreta.getCaminhoImagem())).getImage();
                }
                g.drawImage(cartaSecretaImage, 20, 20, cartaLargura, cartaAltura, null);

                //Cartas Dealer
                for (int i = 0; i < dealerCartas.size(); i++) {
                    Carta carta = dealerCartas.get(i);
                    Image cartaImagem = new ImageIcon(getClass().getResource(carta.getCaminhoImagem())).getImage();
                    g.drawImage(cartaImagem, cartaLargura + 25 + (cartaLargura + 5)*i, 20, cartaLargura, cartaAltura, null);
                }
            
                //Cartas Jogador 
                for (int i = 0; i < jogadorCartas.size(); i++) {
                    Carta carta = jogadorCartas.get(i);
                    Image cartaImagem = new ImageIcon(getClass().getResource(carta.getCaminhoImagem())).getImage();
                    g.drawImage(cartaImagem, 20 + (cartaLargura + 5)*i, 320, cartaLargura, cartaAltura, null);
                }

                //Checar quem ganhou
                if (!botaoParar.isEnabled()) {
                    dealerSoma = diminuirDealerAses();
                    jogadorSoma = diminuirJogadorAses();
                    System.out.println("Resultado do jogo: ");
                    System.out.println("Dealer: " + dealerSoma);
                    System.out.println("Jogador: " + jogadorSoma);

                    String message = "";
                    if (jogadorSoma > 21) {
                        message = "Você perdeu!";
                    }
                    else if (dealerSoma > 21) {
                        message = "Você ganhou!";
                    }
                    else if (jogadorSoma == dealerSoma) {
                        message = "Empate!";
                    }
                    else if (jogadorSoma < dealerSoma) {
                        message = "Você perdeu!";
                    }
                    else if (jogadorSoma > dealerSoma) {
                        message = "Você ganhou!";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.WHITE);
                    g.drawString(message, 220, 250);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    JPanel painelBotoes = new JPanel();
    JButton botaoComprar = new JButton("Comprar");
    JButton botaoParar = new JButton("Parar");
    JButton botaorecomecar = new JButton("Recomeçar");

    VinteUm() {
        iniciarJogo();

        //Iniciar Tela do Jogo
        janelaJogo.setVisible(true);
        janelaJogo.setSize(janelaLargura,janelaAltura);
        janelaJogo.setLocationRelativeTo(null);
        janelaJogo.setResizable(false);
        janelaJogo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        painelJogo.setLayout(new BorderLayout());
        painelJogo.setBackground(new Color(53, 101, 77));
        janelaJogo.add(painelJogo);

        botaoComprar.setFocusable(false);
        botaoParar.setFocusable(false);
        botaorecomecar.setFocusable(false);

        painelBotoes.add(botaoComprar);
        painelBotoes.add(botaoParar);
        painelBotoes.add(botaorecomecar);
        janelaJogo.add(painelBotoes, BorderLayout.SOUTH);



        //Event Listeners Botões
        botaoComprar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                //Comprar carta Jogador
                Carta cartaComprada = baralho.remove(baralho.size()-1);
                jogadorSoma += cartaComprada.getValor();
                jogadorAsesTotal += cartaComprada.isAs() ? 1 : 0;
                jogadorCartas.add(cartaComprada);
                System.out.println("Jogador comprou: " + cartaComprada);

                if (diminuirJogadorAses() >= 21) {
                    botaoComprar.setEnabled(false);
                }

                painelJogo.repaint();
            }
        });

        botaoParar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                botaoComprar.setEnabled(false);
                botaoParar.setEnabled(false);

                //Comprar carta Dealer
                while (dealerSoma < 17 && dealerCartas.size() < 5) {
                    Carta cartaComprada = baralho.remove(baralho.size()-1); //Remove da ultima posição
                    dealerSoma += cartaComprada.getValor();
                    dealerAsesTotal += cartaComprada.isAs() ? 1 : 0;
                    dealerCartas.add(cartaComprada);
                    System.out.println("Dealer comprou: " + cartaComprada);
                }

                painelJogo.repaint();
            }
        });

        botaorecomecar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                botaoComprar.setEnabled(false);
                botaoParar.setEnabled(false);

                System.out.println("Recomeçando!");
                iniciarJogo();

                painelJogo.repaint();
            }
        });

        painelJogo.repaint();
    }

    public void iniciarJogo() {
        criarBaralho();
        embaralhar();
        botaoComprar.setEnabled(true);
        botaoParar.setEnabled(true);

        //Dealer
        dealerCartas = new ArrayList<Carta>();
        dealerSoma = 0;
        dealerAsesTotal = 0;

        //Comprar carta secreta
        dealerCartaSecreta = baralho.remove(baralho.size()-1); //Remove da ultima posição
        dealerSoma += dealerCartaSecreta.getValor();
        dealerAsesTotal += dealerCartaSecreta.isAs() ? 1 : 0; 

        //Comprar carta inicial
        Carta cartaComprada = baralho.remove(baralho.size()-1); //Remove da ultima posição
        dealerSoma += cartaComprada.getValor();
        dealerAsesTotal += cartaComprada.isAs() ? 1 : 0;
        dealerCartas.add(cartaComprada);

        System.out.println("Dealer:");
        System.out.println("Carta Secreta: " + dealerCartaSecreta);
        System.out.println("Cartas: " + dealerCartas);
        System.out.println("Cartas Ases: " + dealerAsesTotal);
        System.out.println("Valor Total: " + dealerSoma);

        //Jogador
        jogadorCartas = new ArrayList<Carta>();
        jogadorAsesTotal = 0;
        jogadorSoma = 0;

        //Comprar cartas iniciais
        for (int i = 0; i < 2; i++) {
            cartaComprada = baralho.remove(baralho.size()-1);
            jogadorSoma += cartaComprada.getValor();
            jogadorAsesTotal += cartaComprada.isAs() ? 1 : 0;
            jogadorCartas.add(cartaComprada);
        }

        System.out.println("Jogador: ");
        System.out.println("Cartas: " + jogadorCartas);
        System.out.println("Cartas Ases: " + jogadorAsesTotal);
        System.out.println("Valor Total: " + jogadorSoma);


    }

    public void criarBaralho() {
        String[] valores = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] naipes = {"C","D","H","S"};

        baralho = new ArrayList<Carta>();

        for (int i = 0; i < naipes.length; i++) {
            for (int j = 0; j < valores.length; j++) {
                Carta carta = new Carta(valores[j],naipes[i]);
                baralho.add(carta);
            }
        }
        System.out.println("Baralho Criado:");
        System.out.println(baralho);
    }

    public void embaralhar() {
        for (int i = 0; i < baralho.size(); i++) {
            int j = random.nextInt(baralho.size());
            Carta cartaAtual = baralho.get(i);
            Carta cartaRandom = baralho.get(j);
            baralho.set(i, cartaRandom);
            baralho.set(j, cartaAtual);
        }
        System.out.println("Baralho embaralhado:");
        System.out.println(baralho);
    }

    public int diminuirJogadorAses() {
        while (jogadorSoma > 21 && jogadorAsesTotal > 0) {
            jogadorSoma -= 10;
            jogadorAsesTotal -= 1;
        }
        return jogadorSoma;
    }

    public int diminuirDealerAses() {
        while (dealerSoma > 21 && dealerAsesTotal > 0) {
            dealerSoma -= 10;
            dealerAsesTotal -= 1;
        }
        return dealerSoma;
    }
}
