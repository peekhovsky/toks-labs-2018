Лаболаторная работа №6
Выполняется на основе ЛР 3. Необходимо
реализовать передачу данных как по TCP,
то есть должно осуществляться подтверждение
передачи. Связь осуществляется
между 2 машинами. Должна обеспечиваться
последовательная доставка корректного пакета.
В дополнении к отправке неправильного пакета
добавляются удаление пакета (то есть до
принимающей стороны он не дойдет) и перепутывание
пакетов (пакеты приходят не в том
порядке в котором их отправили). Для того,
чтобы соблюдать последовательность пакетов
надо добавить в заголовок пакета поля SN и AN.