class Order:
    def __init__(self):
        self.location = None
        self.description = ""
        self.delivery_date = None
        self.delivery_time = None
        self.assigned_drivers = []


class Customer:
    def __init__(self):
        self.orders = []
        self.id = int()


class OrderPlan:
    def __init__(self):
        self.orders = []
        self.route = None


class Car:
    def __init__(self):
        self.size = None
        self.assigned_driver = Driver()


class Driver:
    def __init__(self):
        self.name = ""
        self.assigned_car = Car()
        self.orderPlan = OrderPlan()
        self.assigned_orders = []
        self.speed = float()
        self.position = None
        self.fuel = float()  # ?

"""
принять заказ - дата, время, место, примечания
удалить заказ - id_order
изменить заказ - id_order, [дата, время, место, примечания]
отдать список заказов пользователя - id_user -> все данные по каждому заказу, ...
отдать данные по заказу - id_order -> данные по заказу
отдать данные по курьеру - id_driver -> данные по курьеру

принять координаты водителя
принять завершение заказа

сказать, что водитель превышает
сказать, что водитель отклонился от курса

"""