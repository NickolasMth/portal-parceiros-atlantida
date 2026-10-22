-- V3: clientes fictícios do desafio (Amarelo=Master, Roxo=Subcota G, Verde=Subcota). Remova em produção.
INSERT INTO patrocinador (nome_fantasia, razao_social) VALUES
 ('Cliente Amarelo','Cliente Amarelo S.A.'),
 ('Cliente Roxo','Cliente Roxo Ltda.'),
 ('Cliente Verde','Cliente Verde Ltda.');

INSERT INTO patrocinio (cota_id, status, valor_acordado, evento_id, patrocinador_id)
SELECT c.id, 'ATIVO', c.valor, e.id, p.id
FROM patrocinador p
JOIN cota c ON (p.nome_fantasia = 'Cliente Amarelo' AND c.codigo = 'MASTER')
            OR (p.nome_fantasia = 'Cliente Roxo'    AND c.codigo = 'SUBCOTA_G')
            OR (p.nome_fantasia = 'Cliente Verde'   AND c.codigo = 'SUBCOTA')
CROSS JOIN evento e WHERE e.edicao = 2027;
