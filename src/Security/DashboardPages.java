package Security;

public final class DashboardPages {

	private DashboardPages() {}

	public static String paginaAdmin(User admin, String usersJson) {
		return """
				<!DOCTYPE html>
				<html lang="pt-BR">
				<head>
				  <meta charset="UTF-8">
				  <meta name="viewport" content="width=device-width, initial-scale=1.0">
				  <title>Painel Admin — Vaultra</title>
				  <link rel="preconnect" href="https://fonts.googleapis.com">
				  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
				  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
				  <style>
				    * { box-sizing: border-box; margin: 0; padding: 0; }
				    body {
				      font-family: 'Inter', sans-serif;
				      background-color: #0b0f19;
				      color: #f1f5f9;
				      display: flex;
				      min-height: 100vh;
				      overflow-x: hidden;
				    }
				    
				    /* Sidebar */
				    .sidebar {
				      width: 260px;
				      background-color: #0f172a;
				      border-right: 1px solid #1e293b;
				      display: flex;
				      flex-direction: column;
				      padding: 24px;
				      flex-shrink: 0;
				    }
				    .logo {
				      font-size: 1.4rem;
				      font-weight: 700;
				      color: #3b82f6;
				      margin-bottom: 32px;
				      display: flex;
				      align-items: center;
				      gap: 10px;
				    }
				    .logo span {
				      color: #f1f5f9;
				    }
				    .menu-item {
				      display: flex;
				      align-items: center;
				      gap: 12px;
				      padding: 12px 16px;
				      color: #94a3b8;
				      text-decoration: none;
				      border-radius: 8px;
				      margin-bottom: 8px;
				      font-weight: 500;
				      cursor: pointer;
				      transition: all 0.2s;
				    }
				    .menu-item:hover, .menu-item.active {
				      background-color: #1e293b;
				      color: #f1f5f9;
				    }
				    .menu-item.active {
				      border-left: 4px solid #3b82f6;
				    }
				    .logout-btn {
				      margin-top: auto;
				      color: #f87171;
				    }
				    .logout-btn:hover {
				      background-color: #7f1d1d33;
				    }

				    /* Main Content */
				    .content {
				      flex-grow: 1;
				      padding: 40px;
				      max-width: 1200px;
				      margin: 0 auto;
				      width: 100%%;
				    }
				    header {
				      display: flex;
				      justify-content: space-between;
				      align-items: center;
				      margin-bottom: 40px;
				      border-bottom: 1px solid #1e293b;
				      padding-bottom: 20px;
				    }
				    h1 { font-size: 1.8rem; font-weight: 700; color: #f8fafc; }
				    .user-badge {
				      display: flex;
				      align-items: center;
				      gap: 10px;
				      background-color: #1e293b;
				      padding: 8px 16px;
				      border-radius: 20px;
				      font-size: 0.9rem;
				      border: 1px solid #334155;
				    }
				    .badge-role {
				      background-color: #2563eb;
				      color: white;
				      padding: 2px 8px;
				      border-radius: 4px;
				      font-size: 0.75rem;
				      font-weight: 600;
				    }

				    /* Tab Panels */
				    .tab-panel {
				      display: none;
				    }
				    .tab-panel.active {
				      display: block;
				      animation: fadeIn 0.3s ease-in-out;
				    }
				    @keyframes fadeIn {
				      from { opacity: 0; transform: translateY(10px); }
				      to { opacity: 1; transform: translateY(0); }
				    }

				    /* Cards Grid */
				    .stats-grid {
				      display: grid;
				      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
				      gap: 24px;
				      margin-bottom: 40px;
				    }
				    .card {
				      background-color: #151f32;
				      border: 1px solid #22314d;
				      border-radius: 12px;
				      padding: 24px;
				    }
				    .card-title { font-size: 0.85rem; color: #94a3b8; font-weight: 600; text-transform: uppercase; margin-bottom: 8px; }
				    .card-value { font-size: 2rem; font-weight: 700; color: #f1f5f9; }
				    
				    /* Table & Form container */
				    .section-grid {
				      display: grid;
				      grid-template-columns: 2fr 1fr;
				      gap: 32px;
				      align-items: start;
				    }
				    @media (max-width: 900px) {
				      .section-grid { grid-template-columns: 1fr; }
				    }
				    
				    /* Tables */
				    table {
				      width: 100%%;
				      border-collapse: collapse;
				      background-color: #151f32;
				      border: 1px solid #22314d;
				      border-radius: 12px;
				      overflow: hidden;
				    }
				    th, td {
				      padding: 16px;
				      text-align: left;
				      border-bottom: 1px solid #1e293b;
				    }
				    th {
				      background-color: #0f172a;
				      color: #94a3b8;
				      font-weight: 600;
				      font-size: 0.85rem;
				      text-transform: uppercase;
				    }
				    tr:last-child td { border-bottom: none; }
				    
				    /* Forms */
				    .form-group {
				      margin-bottom: 16px;
				    }
				    label {
				      display: block;
				      font-size: 0.85rem;
				      color: #cbd5e1;
				      margin-bottom: 6px;
				      font-weight: 500;
				    }
				    input, select {
				      width: 100%%;
				      padding: 10px 14px;
				      background-color: #0b0f19;
				      border: 1px solid #334155;
				      border-radius: 8px;
				      color: #f1f5f9;
				      font-family: inherit;
				    }
				    input:focus, select:focus {
				      outline: 2px solid #3b82f6;
				      border-color: #3b82f6;
				    }
				    button {
				      background-color: #2563eb;
				      color: white;
				      padding: 10px 20px;
				      border: none;
				      border-radius: 8px;
				      font-weight: 600;
				      cursor: pointer;
				      transition: background 0.2s;
				    }
				    button:hover { background-color: #1d4ed8; }
				    button.danger {
				      background-color: #ef4444;
				    }
				    button.danger:hover { background-color: #dc2626; }

				    /* Logs console */
				    .console {
				      background-color: #05070f;
				      border: 1px solid #1e293b;
				      border-radius: 12px;
				      padding: 20px;
				      font-family: 'JetBrains Mono', monospace;
				      font-size: 0.9rem;
				      color: #10b981;
				      height: 400px;
				      overflow-y: auto;
				      line-height: 1.6;
				    }
				    .console-line { margin-bottom: 4px; }
				    .console-line.info { color: #3b82f6; }
				    .console-line.warn { color: #f59e0b; }
				    .console-line.err { color: #ef4444; }
				  </style>
				</head>
				<body>
				  <!-- Sidebar -->
				  <div class="sidebar">
				    <div class="logo">
				      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
				        <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="#3B82F6" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				        <path d="M2 17L12 22L22 17" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				        <path d="M2 12L12 17L22 12" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				      </svg>
				      Vaultra <span>Secure</span>
				    </div>
				    
				    <div class="menu-item active" onclick="switchTab('visao-geral')">Visão Geral</div>
				    <div class="menu-item" onclick="switchTab('usuarios')">Gerenciar Usuários</div>
				    <div class="menu-item" onclick="switchTab('sandbox')">Sandbox & Logs</div>
				    
				    <a href="/logout" class="menu-item logout-btn">Sair</a>
				  </div>

				  <!-- Main Content -->
				  <div class="content">
				    <header>
				      <div>
				        <h1>Painel de Controle Administrador</h1>
				        <p style="color: #94a3b8; font-size: 0.9rem; margin-top: 4px;">Gerenciamento de segurança corporativa do servidor</p>
				      </div>
				      <div class="user-badge">
				        <span>%s</span>
				        <span class="badge-role">ADMIN</span>
				      </div>
				    </header>

				    <!-- Tab: Visão Geral -->
				    <div id="visao-geral" class="tab-panel active">
				      <div class="stats-grid">
				        <div class="card">
				          <div class="card-title">Status do Servidor</div>
				          <div class="card-value" style="color: #10b981;">Ativo</div>
				        </div>
				        <div class="card">
				          <div class="card-title">Sessão JWT</div>
				          <div class="card-value">24 Horas</div>
				        </div>
				        <div class="card">
				          <div class="card-title">Total de Usuários</div>
				          <div class="card-value" id="count-usuarios">0</div>
				        </div>
				      </div>

				      <div class="card">
				        <h2 style="font-size: 1.2rem; margin-bottom: 16px;">Políticas de Segurança do Servidor</h2>
				        <ul style="list-style-type: disc; padding-left: 20px; color: #cbd5e1; line-height: 1.8;">
				          <li>Autenticação por token JWT (HMAC-SHA256) persistido em cookies seguros HttpOnly.</li>
				          <li>Criptografia de senhas usando algoritmo SHA-256 com salts individuais de 128-bits.</li>
				          <li>Mapeamento granular de permissões de caminhos com suporte a wildcards (ex: <code>/workspace/modelagem/*</code>).</li>
				          <li>Cabeçalhos de segurança (X-Content-Type, X-Frame-Options, etc.) injetados em todas as respostas HTTP.</li>
				        </ul>
				      </div>
				    </div>

				    <!-- Tab: Gerenciar Usuários -->
				    <div id="usuarios" class="tab-panel">
				      <div class="section-grid">
				        <div>
				          <h2 style="font-size: 1.2rem; margin-bottom: 16px;">Usuários Cadastrados</h2>
				          <table>
				            <thead>
				              <tr>
				                <th>ID</th>
				                <th>Usuário</th>
				                <th>Cargo</th>
				                <th>Ações</th>
				              </tr>
				            </thead>
				            <tbody id="usuarios-table-body">
				              <!-- Preenchido via JS -->
				            </tbody>
				          </table>
				        </div>

				        <div class="card">
				          <h2 style="font-size: 1.2rem; margin-bottom: 16px;">Novo Usuário</h2>
				          <form id="form-criar-usuario" onsubmit="criarUsuario(event)">
				            <div class="form-group">
				              <label for="username">Nome de Usuário</label>
				              <input id="username" type="text" required>
				            </div>
				            <div class="form-group">
				              <label for="senha">Senha</label>
				              <input id="senha" type="password" required>
				            </div>
				            <div class="form-group">
				              <label for="cargo">Cargo</label>
				              <select id="cargo">
				                <option value="MODELADOR">Modelador</option>
				                <option value="ARQUITETO">Arquiteto</option>
				                <option value="DESENVOLVEDOR">Desenvolvedor</option>
				                <option value="VISUALIZADOR">Visualizador</option>
				                <option value="ADMIN">Administrador</option>
				              </select>
				            </div>
				            <button type="submit" style="width: 100%%;">Criar Usuário</button>
				          </form>
				        </div>
				      </div>
				    </div>

				    <!-- Tab: Sandbox & Logs -->
				    <div id="sandbox" class="tab-panel">
				      <h2 style="font-size: 1.2rem; margin-bottom: 16px;">Terminal de Sandbox & Logs do Servidor</h2>
				      <div class="console" id="logs-console">
				        <div class="console-line info">[SISTEMA] Inicializando terminal seguro de auditoria...</div>
				        <div class="console-line">[SISTEMA] Monitorando acessos na rede interna...</div>
				      </div>
				      <div style="margin-top: 16px; display: flex; gap: 10px;">
				        <input type="text" id="sandbox-cmd" placeholder="Executar comando simulado no sandbox..." style="flex-grow: 1;">
				        <button onclick="executarComandoSandbox()">Executar</button>
				      </div>
				    </div>
				  </div>

				  <script>
				    // Inicializar dados de usuários passados pelo backend
				    let usuarios = %s;

				    function renderUsuarios() {
				      const tbody = document.getElementById('usuarios-table-body');
				      document.getElementById('count-usuarios').innerText = usuarios.length;
				      tbody.innerHTML = '';

				      usuarios.forEach(u => {
				        const tr = document.createElement('tr');
				        
				        const tdId = document.createElement('td');
				        tdId.innerText = u.id;
				        tr.appendChild(tdId);

				        const tdUser = document.createElement('td');
				        tdUser.innerText = u.username;
				        tr.appendChild(tdUser);

				        const tdRole = document.createElement('td');
				        tdRole.innerHTML = `<span class="badge-role" style="background-color: ${u.cargo === 'ADMIN' ? '#2563eb' : '#475569'}">${u.cargo_nome}</span>`;
				        tr.appendChild(tdRole);

				        const tdActions = document.createElement('td');
				        if (u.id !== 1) {
				          const btnDel = document.createElement('button');
				          btnDel.className = 'danger';
				          btnDel.style.padding = '4px 8px';
				          btnDel.style.fontSize = '0.8rem';
				          btnDel.innerText = 'Excluir';
				          btnDel.onclick = () => excluirUsuario(u.id);
				          tdActions.appendChild(btnDel);
				        } else {
				          tdActions.innerHTML = '<span style="color: #64748b; font-size: 0.85rem;">Sistema</span>';
				        }
				        tr.appendChild(tdActions);

				        tbody.appendChild(tr);
				      });
				    }

				    function switchTab(tabId) {
				      document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
				      document.querySelectorAll('.menu-item').forEach(m => m.classList.remove('active'));
				      
				      document.getElementById(tabId).classList.add('active');
				      event.currentTarget.classList.add('active');
				      
				      if (tabId === 'sandbox') {
				        scrollLogsToEnd();
				      }
				    }

				    async function criarUsuario(e) {
				      e.preventDefault();
				      const username = document.getElementById('username').value;
				      const senha = document.getElementById('senha').value;
				      const cargo = document.getElementById('cargo').value;

				      try {
				        const response = await fetch('/api/admin/usuarios', {
				          method: 'POST',
				          headers: { 'Content-Type': 'application/json' },
				          body: JSON.stringify({ username, senha, cargo })
				        });
				        
				        const resData = await response.json();
				        if (response.ok) {
				          alert('Usuário criado com sucesso!');
				          document.getElementById('form-criar-usuario').reset();
				          atualizarListaUsuarios();
				          adicionarLog(`[AUDITORIA] Novo usuário criado: ${username} com cargo ${cargo}`, 'info');
				        } else {
				          alert('Erro ao criar usuário: ' + (resData.error || 'Desconhecido'));
				        }
				      } catch (err) {
				        alert('Erro de conexão ao servidor.');
				      }
				    }

				    async function excluirUsuario(id) {
				      if (!confirm('Deseja realmente excluir este usuário?')) return;

				      try {
				        const response = await fetch(`/api/admin/usuarios?id=${id}`, {
				          method: 'DELETE'
				        });
				        
				        if (response.ok) {
				          alert('Usuário excluído com sucesso!');
				          atualizarListaUsuarios();
				          adicionarLog(`[AUDITORIA] Usuário ID ${id} foi excluído do sistema.`, 'warn');
				        } else {
				          const resData = await response.json();
				          alert('Erro ao excluir: ' + (resData.error || 'Desconhecido'));
				        }
				      } catch (err) {
				        alert('Erro de conexão ao servidor.');
				      }
				    }

				    async function atualizarListaUsuarios() {
				      try {
				        const response = await fetch('/api/admin/usuarios');
				        if (response.ok) {
				          usuarios = await response.json();
				          renderUsuarios();
				        }
				      } catch (err) {
				        console.error('Erro ao atualizar lista:', err);
				      }
				    }

				    function adicionarLog(texto, tipo = '') {
				      const consoleEl = document.getElementById('logs-console');
				      const line = document.createElement('div');
				      line.className = 'console-line ' + tipo;
				      const agora = new Date().toLocaleTimeString();
				      line.innerText = `[${agora}] ${texto}`;
				      consoleEl.appendChild(line);
				      scrollLogsToEnd();
				    }

				    function scrollLogsToEnd() {
				      const consoleEl = document.getElementById('logs-console');
				      consoleEl.scrollTop = consoleEl.scrollHeight;
				    }

				    function executarComandoSandbox() {
				      const cmdInput = document.getElementById('sandbox-cmd');
				      const cmd = cmdInput.value.trim();
				      if (!cmd) return;

				      adicionarLog(`$ ${cmd}`);
				      cmdInput.value = '';

				      // Simulação simples de sandbox
				      setTimeout(() => {
				        if (cmd.toLowerCase() === 'help') {
				          adicionarLog('Comandos disponíveis: help, stats, security-check, clear', 'info');
				        } else if (cmd.toLowerCase() === 'stats') {
				          adicionarLog(`Memória utilizada: ${(performance.memory ? Math.round(performance.memory.usedJSHeapSize / 1024 / 1024) + 'MB' : 'Não suportado pelo browser')}`, 'info');
				        } else if (cmd.toLowerCase() === 'security-check') {
				          adicionarLog('[SUCESSO] Varredura concluída. 0 vulnerabilidades detectadas.', 'info');
				        } else if (cmd.toLowerCase() === 'clear') {
				          document.getElementById('logs-console').innerHTML = '';
				        } else {
				          adicionarLog(`Comando não reconhecido: ${cmd}. Digite 'help' para comandos do sandbox.`, 'err');
				        }
				      }, 300);
				    }

				    // Render inicial
				    renderUsuarios();
				  </script>
				</body>
				</html>
				""".formatted(admin.getUsername(), usersJson);
	}

	public static String paginaUserHome(User user) {
		StringBuilder permsList = new StringBuilder();
		if (user.getPermissoes().isEmpty()) {
			permsList.append("<li>Nenhuma permissão específica configurada.</li>");
		} else {
			for (Permissao p : user.getPermissoes()) {
				permsList.append("<li>Caminho: <code>%s</code> | Visualizar: <b>%s</b> | Editar: <b>%s</b></li>"
						.formatted(p.getCaminhoPermitido(), p.isPodeVer() ? "Sim" : "Não", p.isPodeEditar() ? "Sim" : "Não"));
			}
		}

		return """
				<!DOCTYPE html>
				<html lang="pt-BR">
				<head>
				  <meta charset="UTF-8">
				  <meta name="viewport" content="width=device-width, initial-scale=1.0">
				  <title>Área de Trabalho — Vaultra</title>
				  <link rel="preconnect" href="https://fonts.googleapis.com">
				  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
				  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
				  <style>
				    * { box-sizing: border-box; margin: 0; padding: 0; }
				    body {
				      font-family: 'Inter', sans-serif;
				      background-color: #0b0f19;
				      color: #f1f5f9;
				      display: flex;
				      min-height: 100vh;
				    }
				    
				    /* Sidebar */
				    .sidebar {
				      width: 260px;
				      background-color: #0f172a;
				      border-right: 1px solid #1e293b;
				      display: flex;
				      flex-direction: column;
				      padding: 24px;
				    }
				    .logo {
				      font-size: 1.4rem;
				      font-weight: 700;
				      color: #3b82f6;
				      margin-bottom: 32px;
				      display: flex;
				      align-items: center;
				      gap: 10px;
				    }
				    .logo span { color: #f1f5f9; }
				    .menu-item {
				      display: flex;
				      align-items: center;
				      gap: 12px;
				      padding: 12px 16px;
				      color: #94a3b8;
				      text-decoration: none;
				      border-radius: 8px;
				      margin-bottom: 8px;
				      font-weight: 500;
				      cursor: pointer;
				      transition: all 0.2s;
				    }
				    .menu-item:hover, .menu-item.active {
				      background-color: #1e293b;
				      color: #f1f5f9;
				    }
				    .menu-item.active {
				      border-left: 4px solid #3b82f6;
				    }
				    .logout-btn {
				      margin-top: auto;
				      color: #f87171;
				    }
				    .logout-btn:hover {
				      background-color: #7f1d1d33;
				    }

				    /* Main Content */
				    .content {
				      flex-grow: 1;
				      padding: 40px;
				      max-width: 1000px;
				      margin: 0 auto;
				      width: 100%%;
				    }
				    header {
				      display: flex;
				      justify-content: space-between;
				      align-items: center;
				      margin-bottom: 40px;
				      border-bottom: 1px solid #1e293b;
				      padding-bottom: 20px;
				    }
				    h1 { font-size: 1.8rem; font-weight: 700; color: #f8fafc; }
				    .user-badge {
				      display: flex;
				      align-items: center;
				      gap: 10px;
				      background-color: #1e293b;
				      padding: 8px 16px;
				      border-radius: 20px;
				      font-size: 0.9rem;
				      border: 1px solid #334155;
				    }
				    .badge-role {
				      background-color: #475569;
				      color: white;
				      padding: 2px 8px;
				      border-radius: 4px;
				      font-size: 0.75rem;
				      font-weight: 600;
				    }

				    /* Tab Panels */
				    .tab-panel {
				      display: none;
				    }
				    .tab-panel.active {
				      display: block;
				      animation: fadeIn 0.3s ease-in-out;
				    }
				    @keyframes fadeIn {
				      from { opacity: 0; transform: translateY(10px); }
				      to { opacity: 1; transform: translateY(0); }
				    }

				    .card {
				      background-color: #151f32;
				      border: 1px solid #22314d;
				      border-radius: 12px;
				      padding: 24px;
				      margin-bottom: 24px;
				    }
				    .card-title { font-size: 1.1rem; color: #f1f5f9; font-weight: 600; margin-bottom: 16px; }
				    
				    ul {
				      list-style-type: none;
				      line-height: 2;
				    }
				    li {
				      padding: 8px 0;
				      border-bottom: 1px solid #1e293b;
				    }
				    li:last-child { border-bottom: none; }
				    code {
				      background-color: #0f172a;
				      padding: 2px 6px;
				      border-radius: 4px;
				      font-family: 'JetBrains Mono', monospace;
				    }

				    /* Sandbox console */
				    .console {
				      background-color: #05070f;
				      border: 1px solid #1e293b;
				      border-radius: 12px;
				      padding: 20px;
				      font-family: 'JetBrains Mono', monospace;
				      font-size: 0.9rem;
				      color: #3b82f6;
				      height: 350px;
				      overflow-y: auto;
				      line-height: 1.6;
				    }
				  </style>
				</head>
				<body>
				  <!-- Sidebar -->
				  <div class="sidebar">
				    <div class="logo">
				      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
				        <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="#3B82F6" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				        <path d="M2 17L12 22L22 17" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				        <path d="M2 12L12 17L22 12" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				      </svg>
				      Vaultra <span>Workspace</span>
				    </div>
				    
				    <div class="menu-item active" onclick="switchTab('perfil')">Meu Perfil</div>
				    <div class="menu-item" onclick="switchTab('sandbox')">Sandbox Privado</div>
				    
				    <a href="/logout" class="menu-item logout-btn">Sair</a>
				  </div>

				  <!-- Main Content -->
				  <div class="content">
				    <header>
				      <div>
				        <h1>Área de Trabalho Segura</h1>
				        <p style="color: #94a3b8; font-size: 0.9rem; margin-top: 4px;">Bem-vindo ao servidor HTTP Vaultra</p>
				      </div>
				      <div class="user-badge">
				        <span>%s</span>
				        <span class="badge-role">%s</span>
				      </div>
				    </header>

				    <!-- Tab: Perfil -->
				    <div id="perfil" class="tab-panel active">
				      <div class="card">
				        <div class="card-title">Informações de Credenciais & Cargo</div>
				        <table style="width: 100%%; border-collapse: collapse;">
				          <tr style="border-bottom: 1px solid #1e293b;">
				            <td style="padding: 12px 0; color: #94a3b8; font-weight: 500;">Usuário</td>
				            <td style="padding: 12px 0; text-align: right; font-weight: 600;">%s</td>
				          </tr>
				          <tr style="border-bottom: 1px solid #1e293b;">
				            <td style="padding: 12px 0; color: #94a3b8; font-weight: 500;">Cargo Atribuído</td>
				            <td style="padding: 12px 0; text-align: right; font-weight: 600; color: #3b82f6;">%s</td>
				          </tr>
				          <tr>
				            <td style="padding: 12px 0; color: #94a3b8; font-weight: 500;">Descrição do Acesso</td>
				            <td style="padding: 12px 0; text-align: right; color: #cbd5e1; font-size: 0.9rem;">%s</td>
				          </tr>
				        </table>
				      </div>

				      <div class="card">
				        <div class="card-title">Permissões de Diretórios</div>
				        <ul>
				          %s
				        </ul>
				      </div>
				    </div>

				    <!-- Tab: Sandbox -->
				    <div id="sandbox" class="tab-panel">
				      <h2 style="font-size: 1.2rem; margin-bottom: 16px;">Console Sandbox Restrito para %s</h2>
				      <div class="console">
				        <div>[SISTEMA] Inicializando sandbox para cargo %s...</div>
				        <div>[INFO] Conexão segura estabelecida.</div>
				        <div>[PERMISSÕES] Suas restrições de escrita/leitura foram carregadas com sucesso.</div>
				      </div>
				    </div>
				  </div>

				  <script>
				    function switchTab(tabId) {
				      document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
				      document.querySelectorAll('.menu-item').forEach(m => m.classList.remove('active'));
				      
				      document.getElementById(tabId).classList.add('active');
				      event.currentTarget.classList.add('active');
				    }
				  </script>
				</body>
				</html>
				""".formatted(user.getUsername(), user.getCargo().getNome(),
						user.getUsername(), user.getCargo().name(), user.getCargo().getDescricao(),
						permsList.toString(), user.getUsername(), user.getCargo().name());
	}
}
