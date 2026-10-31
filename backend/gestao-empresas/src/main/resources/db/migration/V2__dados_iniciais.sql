INSERT INTO modulo (nome, codigo, descricao, ordem) VALUES
 ('Perfil do cliente','PERFIL','Aba 1',1),
 ('Aplicações de marca','APLICACOES','Aba 2',2),
 ('Mídia e conteúdo','MIDIA','Aba 3',3),
 ('Cortesias','CORTESIAS','Aba 4',4),
 ('Credenciamento','CREDENCIAMENTO','Aba 5',5),
 ('Pesquisa da ativação','PESQUISA','Aba 6',6),
 ('Contrato','CONTRATO','Aba 7',7);

INSERT INTO perfil (codigo, nome, descricao) VALUES
 ('ADMIN_RBS','Admin RBS','Time B2B, produção e pesquisa'),
 ('ADMIN_PATROCINADOR','Admin Patrocinador','Clientes e agências');
INSERT INTO role (codigo, nome, descricao) VALUES
 ('ADMIN_RBS','Admin RBS','Gerencia o portal'),
 ('ADMIN_PATROCINADOR','Admin Patrocinador','Acessa o próprio patrocínio');
INSERT INTO perfil_role (perfil_id, role_id)
SELECT p.id, r.id FROM perfil p JOIN role r ON r.codigo = p.codigo;

INSERT INTO permissao (codigo, nome, descricao) VALUES
 ('PATROCINADOR_GERENCIAR','Gerenciar patrocinadores','Criar, editar e inativar patrocinadores'),
 ('PATROCINIO_GERENCIAR','Gerenciar patrocínios','Vincular patrocinador, cota e evento'),
 ('EMAIL_LIBERAR','Liberar e-mails','Convidar pessoas para acessar o portal'),
 ('USUARIO_LER','Listar usuários','Ver usuários cadastrados');

INSERT INTO role_permissao (role_id, permissao_id)
SELECT r.id, p.id FROM role r CROSS JOIN permissao p WHERE r.codigo = 'ADMIN_RBS';
INSERT INTO role_permissao (role_id, permissao_id)
SELECT r.id, p.id FROM role r JOIN permissao p ON p.codigo = 'EMAIL_LIBERAR' WHERE r.codigo = 'ADMIN_PATROCINADOR';

INSERT INTO evento (nome, edicao, descricao,data_inicio,data_fim,local, status) VALUES ('Planeta Atlântida', 2027, 'Edição 2027', '2026-12-30','2027-03-15','São Jeronimo','ATIVO');

INSERT INTO plano_comercial (nome, descricao, status, evento_id)
SELECT 'Plano 2027', 'Plano comercial base', 'ATIVO', e.id FROM evento e WHERE e.edicao = 2027;

INSERT INTO cota (codigo, nome, plano_comercial_id, descricao, valor, limite_patrocinio, ordem, beneficios)
SELECT v.codigo, v.nome, pc.id, v.descricao, v.valor, v.limite, v.ordem, v.beneficios::jsonb
FROM plano_comercial pc, (VALUES
 ('MASTER','Master','Módulo mais completo',1000000.00,6,1,'{"max_usuarios":10,"pesquisa_perguntas":3,"planilha_mensal":true}'),
 ('SUBCOTA_G','Subcota G','Módulo intermediário',500000.00,5,2,'{"max_usuarios":5,"planilha_mensal":false}'),
 ('SUBCOTA','Subcota','Módulo básico',100000.00,20,3,'{"max_usuarios":2,"planilha_mensal":false}')
) AS v(codigo, nome, descricao, valor, limite, ordem, beneficios)
WHERE pc.nome = 'Plano 2027';


INSERT INTO cota_modulo (modulo_id, cota_id, habilitado)
SELECT m.id, c.id, NOT (m.codigo = 'PESQUISA' AND c.codigo <> 'MASTER')
FROM modulo m CROSS JOIN cota c;
