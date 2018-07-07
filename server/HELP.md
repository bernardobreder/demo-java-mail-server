# THE SMTP MODEL

O design do SMTP baseia-se no seguinte modelo de comunicação: como resultado de uma solicitação de email do usuário, o remetente-SMTP estabelece um canal de transmissão bidirecional para um receptor SMTP. O receptor SMTP pode ser o destino final ou intermediário. Comandos SMTP são gerados pelo remetente-SMTP e enviados para o receptor-SMTP. As respostas SMTP são enviadas do receptor-SMTP para o remetente-SMTP em resposta aos comandos.

Depois que o canal de transmissão é estabelecido, o remetente SMTP envia um comando MAIL indicando o remetente do email. Se o destinatário SMTP aceitar o correio, ele responderá com uma resposta OK. O remetente SMTP envia um comando RCPT identificando um destinatário do email. Se o receptor de SMTP puder aceitar correio para esse destinatário, ele responderá com uma resposta OK; se não, responde com uma resposta rejeitando aquele destinatário (mas não toda a transação de correio). O remetente SMTP e o destinatário SMTP podem negociar vários destinatários. Quando os destinatários foram negociados, o remetente SMTP envia os dados de correio, terminando com uma sequência especial. Se o receptor SMTP processar com êxito os dados de correio, ele responderá com uma resposta OK. A caixa de diálogo é propositadamente bloqueada, uma de cada vez.

               +----------+                +----------+
   +------+    |          |                |          |
   | User |<-->|          |      SMTP      |          |
   +------+    |  Sender- |Commands/Replies| Receiver-|
   +------+    |   SMTP   |<-------------->|    SMTP  |    +------+
   | File |<-->|          |    and Mail    |          |<-->| File |
   |System|    |          |                |          |    |System|
   +------+    +----------+                +----------+    +------+
   

                Sender-SMTP                Receiver-SMTP

O SMTP fornece mecanismos para a transmissão de correio; diretamente do host do usuário remetente para o host do usuário destinatário quando os dois hosts estiverem conectados ao mesmo serviço de transporte ou por meio de um ou mais servidores SMTP de retransmissão quando os hosts de origem e destino não estiverem conectados ao mesmo serviço de transporte.

Para poder fornecer a capacidade de retransmissão, o servidor SMTP deve receber o nome do host de destino final, bem como o nome da caixa de correio de destino.

O argumento para o comando MAIL é um caminho inverso, que especifica de quem é o correio. O argumento para o comando RCPT é um caminho de encaminhamento, que especifica para quem é o correio. O caminho de encaminhamento é uma rota de origem, enquanto o caminho inverso é uma rota de retorno (que pode ser usada para retornar uma mensagem ao remetente quando ocorre um erro com uma mensagem retransmitida).

Quando a mesma mensagem é enviada para vários destinatários, o SMTP estimula a transmissão de apenas uma cópia dos dados para todos os destinatários no mesmo host de destino.

Os comandos e respostas de correio têm uma sintaxe rígida. As respostas também possuem um código numérico. A seguir, são exibidos exemplos que usam comandos e respostas reais. As listas completas de comandos e respostas aparecem na Seção 4 das especificações.

Comandos e respostas não diferenciam maiúsculas de minúsculas. Ou seja, uma palavra de comando ou resposta pode ser maiúscula, minúscula ou qualquer mistura de maiúsculas e minúsculas. Observe que isso não é verdadeiro para os nomes de usuários da caixa de correio. Para alguns hosts, o nome do usuário faz distinção entre maiúsculas e minúsculas, e as implementações de SMTP precisam levar em conta o caso para preservar o caso dos nomes de usuários, conforme aparecem nos argumentos da caixa de correio. Os nomes de host não diferenciam maiúsculas de minúsculas.

Comandos e respostas são compostos de caracteres do conjunto de caracteres ASCII [1]. Quando o serviço de transporte fornece um canal de transmissão de 8 bits (octeto), cada caractere de 7 bits é transmitido à direita justificado em um octeto com o bit de alta ordem desmarcado para zero.

Ao especificar a forma geral de um comando ou resposta, um argumento (ou símbolo especial) será denotado por uma variável meta-linguística (ou constante), por exemplo, "<string>" ou "<reverse-path>". Aqui, os colchetes angulares indicam que são variáveis ​​meta-lingüísticas. No entanto, alguns argumentos usam os colchetes angulares literalmente. Por exemplo, um caminho reverso real é colocado entre colchetes angulares, isto é, "<John.Smith@USC-ISI.ARPA>" é uma instância de <caminho inverso> (os colchetes angulares são realmente transmitidos no comando ou resposta).