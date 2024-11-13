#include <SoftwareSerial.h>
#include <Servo.h>

// Configura os pinos dos servos
Servo polegar;
Servo indicador; 
Servo medio; 
Servo anelar;
Servo midinho;

// variável para armazenar o comando enviado pelo app
char comando;

// Variáveis para controlar os estados dos dedos (1 = aberto, 0 = fechado)
int estadoPolegar = 1;
int estadoIndicador = 1;
int estadomedio = 1;
int estadoAnelar = 1;
int estadoMidinho = 1;

void setup() {
  Serial.begin(9600);
  delay(500);
}

void loop() {
  //chamo função para verificar comandos do app
  verificaComandoCelular();
}

void verificaComandoCelular(){
  //contador para sair do loop de espera de mensagens via app
  int contador = 0;

  //verifico se há dados disponíveis para serem lidos na porta serial
  while(Serial.available() > 0){
    //Serial.println("esperando mensagem");
    comando = Serial.read();
    
    // Verificar se algo foi recebido
    if (comando != '\0'){
      
      //Serial.print("comando: ");
      //Serial.println(comando);
      
      //chamo a função para processar o comando
      processarComando(comando);
    }
    // Se ficar mais de 5 segundos no loop, sai automaticamente
    delay(100);
    contador++;
    if (contador > 50){
      break;
    }
  }
 }

//função para executar os comandos caso seja um comando válido
//todos os ifs verificam se o motor já está na posição desejada
void processarComando(char cmd) {
  //Serial.print("comando recebido: ");
  //Serial.println(cmd);

  switch (cmd) {
    case 'A': // Fechar mão
      fecharMao();
      break;
    case 'B': // Abrir mão
      abrirMao();
      break;
    case 'C': // Abrir polegar
      if (estadoPolegar == 1) {
        conectaMotor();
        polegar.write(88);
        delay(4000);
        polegar.write(91);
        estadoPolegar = 0;
        desconectaMotor();
      }
      break;
    case 'D': // Abrir indicador
      if (estadoIndicador == 1) {
        conectaMotor();
        indicador.write(88);
        delay(4000);
        indicador.write(91);
        estadoIndicador = 0;
        desconectaMotor();
      }
      break;
    case 'E': // Abrir médio
      if (estadomedio == 1) {
        conectaMotor();
        medio.write(94);
        delay(4000);
        medio.write(91);
        estadomedio = 0;
        desconectaMotor();
      }
      break;
    case 'F': // Abrir anelar
      if (estadoAnelar == 1) {
        conectaMotor();
        anelar.write(94);
        delay(4000);
        anelar.write(91);
        estadoAnelar = 0;
        desconectaMotor();
      }
      break;
    case 'G': // Abrir mindinho
      if (estadoMidinho == 1) {
        conectaMotor();
        midinho.write(94);
        delay(4000);
        midinho.write(91);
        estadoMidinho = 0;
        desconectaMotor();
      }
      break;
    case 'H': // Fechar polegar
      if (estadoPolegar == 0) {
        conectaMotor();
        polegar.write(94);
        delay(4000);
        polegar.write(91);
        estadoPolegar = 1;
        desconectaMotor();
      }
      break;
    case 'I': // Fechar indicador
      if (estadoIndicador == 0) {
        conectaMotor();
        indicador.write(94);
        delay(4000);
        indicador.write(91);
        estadoIndicador = 1;
        desconectaMotor();
      }
      break;
    case 'J': // Fechar médio
      if (estadomedio == 0) {
        conectaMotor();
        medio.write(88);
        delay(4000);
        medio.write(91);
        estadomedio = 1;
        desconectaMotor();
      }
      break;
    case 'K': // Fechar anelar
      if (estadoAnelar == 0) {
        conectaMotor();
        anelar.write(88);
        delay(4000);
        anelar.write(91);
        estadoAnelar = 1;
        desconectaMotor();
      }
      break;
    case 'L': // Fechar mindinho
      if (estadoMidinho == 0) {
        conectaMotor();
        midinho.write(88);
        delay(4000);
        midinho.write(91);
        estadoMidinho = 1;
        desconectaMotor();
      }
      break;
    case 'M': // Abrir pinça
       if (estadoPolegar == 1) {
        conectaMotor();
        polegar.write(88);
        delay(4000);
        polegar.write(91);
        estadoPolegar = 0;
        desconectaMotor();
      }
      if (estadoIndicador == 1) {
        conectaMotor();
        indicador.write(88);
        delay(4000);
        indicador.write(91);
        estadoIndicador = 0;
        desconectaMotor();
      }
      break;
    case 'N': // Fechar pinça
      if (estadoPolegar == 0) {
        conectaMotor();
        polegar.write(94);
        delay(4000);
        polegar.write(91);
        estadoPolegar = 1;
        desconectaMotor();
      }
      if (estadoIndicador == 0) {
        conectaMotor();
        indicador.write(94);
        delay(4000);
        indicador.write(91);
        estadoIndicador = 1;
        desconectaMotor();
      }
      break;
    default:
      //Serial.println("Comando inválido");
      break;
  }
}

void abrirMao() {
  conectaMotor();
  if (estadoPolegar == 1) {
    polegar.write(88);
    delay(4000);
    polegar.write(91);
    estadoPolegar = 0;
  }
  if (estadoIndicador == 1) {
    indicador.write(88);
    delay(4000);
    indicador.write(91);
    estadoIndicador = 0;
  }
  if (estadomedio == 1) {
    medio.write(94);
    delay(4000);
    medio.write(91);
    estadomedio = 0;
  }
  if (estadoAnelar == 1) {
    anelar.write(94);
    delay(4000); 
    anelar.write(91);
    estadoAnelar = 0;
  }
  if (estadoMidinho == 1) {
    midinho.write(94);
    delay(4000);
    midinho.write(91);
    estadoMidinho = 0;
  }
  desconectaMotor();
}

void fecharMao() {
  conectaMotor();
  if (estadoPolegar == 0) {
    polegar.write(94);
    delay(4000);
    polegar.write(91);
    estadoPolegar = 1;
  }
  if (estadoIndicador == 0) {
    indicador.write(94);
    delay(4000);
    indicador.write(91);
    estadoIndicador = 1;
  }
  if (estadomedio == 0) {
    medio.write(88);
    delay(4000);
    medio.write(91);
    estadomedio = 1;
  }
  if (estadoAnelar == 0) {
    anelar.write(88);
    delay(4000);
    anelar.write(91);
    estadoAnelar = 1;
  }
  if (estadoMidinho == 0) {
    midinho.write(88);
    delay(4000);
    midinho.write(91);
    estadoMidinho = 1;
  }
  desconectaMotor();
}
//função para desconectar os motores
void desconectaMotor() {
  polegar.detach();
  indicador.detach();
  medio.detach();
  anelar.detach();
  midinho.detach();
  delay(1000);
}

//função para conectar aos motores
void conectaMotor() {
  delay(500);
  //conecto e desligo, para não girar sem dar o comando
  polegar.attach(9);
  polegar.write(91);
  
  indicador.attach(8);
  indicador.write(91);
  
  medio.attach(7);
  medio.write(91);
  
  anelar.attach(6);
  anelar.write(91);
  
  midinho.attach(5);
  midinho.write(91);
  delay(500);
}

//função para desligar os motores
void desligaMotor() {
  polegar.write(91);
  indicador.write(91);
  medio.write(91);
  anelar.write(91);
  midinho.write(91);
  delay(500);
}
