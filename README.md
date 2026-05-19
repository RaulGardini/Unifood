# UniFood

Aplicativo Android para pedidos de comida no campus universitário.

## Configuração do Firebase

Este projeto utiliza Firebase Authentication e Firestore. O arquivo `google-services.json` não está incluso no repositório por segurança. Para rodar o projeto, siga os passos abaixo:

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Crie um novo projeto ou peça acesso ao projeto **unifood-dbc43**
3. No projeto, vá em **Configurações do projeto** (ícone de engrenagem) > **Geral**
4. Em **Seus apps**, clique em **Adicionar app** > **Android**
5. Preencha o package name: `com.example.unifood`
6. Faça o download do arquivo `google-services.json`
7. Copie o arquivo para a pasta `app/` do projeto (na raiz do módulo app)
8. No Firebase Console, ative os seguintes serviços:
   - **Authentication** > Sign-in method > **Email/password** (ativar)
   - **Firestore Database** > Criar banco de dados

### Estrutura do Firestore

Coleção `usuarios` — cada documento usa o UID do Firebase Auth como ID:

| Campo      | Tipo   | Exemplo            |
|------------|--------|--------------------|
| nome       | string | "João Silva"       |
| email      | string | "joao@unifor.br"   |
| matricula  | string | "2112345"          |
| tipo       | string | "aluno", "admin" ou "lojista" |
